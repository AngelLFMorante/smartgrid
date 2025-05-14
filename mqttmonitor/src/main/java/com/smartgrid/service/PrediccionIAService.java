package com.smartgrid.service;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.translate.TranslateException;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

import com.smartgrid.repository.MedicionRepository;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PrediccionIAService {

    private final MedicionRepository medicionRepository;

    public PrediccionIAService(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
    }

    public double[] predecirConsumo(int pasos) throws IOException, ModelException, TranslateException {
        // Paso 1: Obtener la serie de consumo total (últimos N minutos)
        List<Double> historial = medicionRepository.getConsumoTotalAgrupadoPorMinuto();

        if (historial.size() < 20) {
            throw new IllegalArgumentException("No hay suficientes datos históricos para predecir.");
        }

        // Paso 2: Convertir a NDArray
        try (NDManager manager = NDManager.newBaseManager()) {

            float[] data = new float[historial.size()];
            for (int i = 0; i < historial.size(); i++) {
                data[i] = historial.get(i).floatValue();
            }

            NDArray inputArray = manager.create(data).reshape(1, data.length);

            // Paso 3: Configurar modelo preentrenado de DJL
            Criteria<NDArray, NDArray> criteria = Criteria.builder()
                    .setTypes(NDArray.class, NDArray.class)
                    .optEngine("PyTorch")
                    .optModelPath(Paths.get(getClass().getClassLoader().getResource("model/lstm_model.pt").toURI()).getParent())
                    .optModelName("lstm_model")
                    .build();

            try (Predictor<NDArray, NDArray> predictor = ModelZoo.loadModel(criteria).newPredictor()) {
                NDArray result = predictor.predict(inputArray);

                float[] prediccion = result.toFloatArray();
                double[] resultadoFinal = new double[pasos];

                System.arraycopy(prediccion, 0, resultadoFinal, 0, Math.min(pasos, prediccion.length));
                return resultadoFinal;
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
