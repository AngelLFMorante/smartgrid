# 🔌 SmartGrid - Predicción de Consumo Eléctrico con LSTM y DJL

Este proyecto predice el consumo eléctrico de un sistema Smart Grid utilizando un modelo LSTM entrenado previamente en PyTorch e integrado en Java con la librería DJL (Deep Java Library).

---

## 📁 Estructura del Proyecto

```
smartgrid/
├── src/
│   └── main/
│       └── java/
│           └── com/smartgrid/service/PrediccionIAService.java
│       └── resources/
│           └── model/
│               └── lstm_model.pt
├── pom.xml
└── README.md
```

---

## ⚙️ ¿Qué hace este proyecto?

- Lee el historial de consumo eléctrico desde la base de datos (`medicionRepository`).
- Preprocesa los datos y los transforma en un `NDArray` para el modelo.
- Carga el modelo LSTM previamente entrenado con PyTorch (formato `.pt`).
- Realiza la predicción de consumo futuro en pasos de minutos.
- Convierte las predicciones por minuto a estimaciones horarias (Wh).
- Calcula la media y el pico de consumo estimado para la próxima hora.
- Loggea información clara con la hora exacta en formato `HH:mm` para facilitar la interpretación.
- Comprueba si el pico de consumo excede un límite definido y genera alertas y recomendaciones en el log.

---

## 🧠 Modelo Usado

- Nombre: `lstm_model.pt`
- Tipo: LSTM
- Formato: TorchScript (`.pt`)
- Entrenado en: PyTorch + Jupyter Notebook
- Ubicación en el proyecto: `src/main/resources/model/lstm_model.pt`

---

## 🧪 Cómo se entrenó el modelo

1. Se descargó desde GitHub o Kaggle el siguiente proyecto en Jupyter:  
   https://github.com/iamirmasoud/energy_consumption_prediction.git

```
Nombre del notebook: Energy consumption prediction using LSTM-GRU in PyTorch.ipynb
Contenía:
- Carpeta /data con CSV de consumo
- Notebook entrenando modelos LSTM y GRU
- Exportación final como lstm_model.pt y gru_model.pt
```

2. Dependencias de Python usadas para entrenamiento:

```bash
pip install torch pandas matplotlib scikit-learn jupyter
```

3. Se ejecutó el notebook para entrenar y validar modelos.

4. El archivo `lstm_model.pt` fue exportado desde PyTorch y añadido al proyecto Java bajo `resources/model/`.

---

## 🔧 Dependencias necesarias en Java

Asegúrate de tener estas dependencias en tu `pom.xml` para usar DJL con PyTorch:

```xml
<dependencies>
    <dependency>
        <groupId>ai.djl.pytorch</groupId>
        <artifactId>pytorch-engine</artifactId>
        <version>0.26.0</version>
    </dependency>
    <dependency>
        <groupId>ai.djl.pytorch</groupId>
        <artifactId>pytorch-model-zoo</artifactId>
        <version>0.26.0</version>
    </dependency>
    <dependency>
        <groupId>ai.djl.api</groupId>
        <artifactId>api</artifactId>
        <version>0.26.0</version>
    </dependency>
</dependencies>
```

---

## ▶️ Cómo ejecutar

1. Asegúrate de tener los datos históricos en tu base de datos (mínimo 20 minutos).
2. Ejecuta tu aplicación Spring Boot.
3. La clase `PrediccionIAService` se encargará de:
   - Cargar el modelo
   - Leer historial
   - Predecir los próximos pasos (predicción por minuto)
4. Los resultados se convierten a estimaciones horarias (Wh) y se calcula media y pico.
5. En los logs verás la predicción horaria con formato de hora siguiente (HH:mm) y recomendaciones si el consumo supera el límite.

---

## 📌 Notas adicionales

- El modelo fue entrenado con una entrada de serie temporal de 30 pasos (por ejemplo, 30 minutos).
- Si cambias el modelo, ajusta también el preprocesado de datos en Java para que coincida con la forma esperada.

---

## 🧑‍💻 Contacto

Creado por Ángel Fernández Mora.  
Modelo y notebook base extraídos de proyectos públicos de aprendizaje automático.
