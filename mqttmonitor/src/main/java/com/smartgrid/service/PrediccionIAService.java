package com.smartgrid.service;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.translate.TranslateException;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

import com.smartgrid.repository.MedicionRepository;
import com.smartgrid.translator.LSTMTranslator;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Servicio encargado de ejecutar la predicción de consumo eléctrico futuro
 * utilizando un modelo de Deep Learning preentrenado (TorchScript).
 * <p>
 * Este servicio simula una predicción multi-step encadenando la salida del modelo
 * como parte de la entrada para el siguiente paso. Cada predicción representa
 * una hora futura, no minutos.
 */
@Service
public class PrediccionIAService {

    private static final Logger log = LoggerFactory.getLogger(PrediccionIAService.class);

    private final MedicionRepository medicionRepository;
    private Predictor<NDArray, NDArray> predictor;
    private NDManager manager;

    public PrediccionIAService(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
    }

    @PostConstruct
    public void init() {
        try {
            manager = NDManager.newBaseManager();

            URL modelUrl = getClass().getClassLoader().getResource("model/torchscript_model.pt");
            if (modelUrl == null) {
                throw new IllegalStateException("❌ No se encontró el archivo del modelo.");
            }

            Criteria<NDArray, NDArray> criteria = Criteria.builder()
                    .setTypes(NDArray.class, NDArray.class)
                    .optEngine("PyTorch")
                    .optModelPath(Paths.get(modelUrl.toURI()).getParent())
                    .optModelName("torchscript_model")
                    .optTranslator(new LSTMTranslator())
                    .build();

            predictor = ModelZoo.loadModel(criteria).newPredictor();
            log.info("✅ Modelo cargado correctamente");

        } catch (Exception e) {
            log.error("❌ Error al cargar el modelo:", e);
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void close() {
        if (predictor != null) predictor.close();
        if (manager != null) manager.close();
    }

    public double[] predecirConsumoProximoBloque(int pasos) throws TranslateException {
        List<Double> historial = medicionRepository.getConsumoTotalAgrupadoPorMinuto();

        int timeSteps = 10;
        int features = 1;

        if (historial.size() < timeSteps) {
            throw new IllegalArgumentException("No hay suficientes datos históricos para predecir.");
        }

        List<Double> ventana = new ArrayList<>(historial.subList(historial.size() - timeSteps, historial.size()));
        double[] resultados = new double[pasos];

        for (int i = 0; i < pasos; i++) {
            float[] inputData = new float[timeSteps * features];
            for (int j = 0; j < timeSteps; j++) {
                inputData[j] = ventana.get(j).floatValue();
            }

            NDArray inputArray = manager.create(inputData).reshape(1, timeSteps, features);
            NDArray salida = predictor.predict(inputArray);
            float[] salidaFloat = salida.toFloatArray();
            double prediccion = salidaFloat[0];

            resultados[i] = prediccion;
            log.info("🔮 Predicción paso {}: {}W", i + 1, prediccion);

            ventana.remove(0);
            ventana.add(prediccion);
        }

        return resultados;
    }
}
