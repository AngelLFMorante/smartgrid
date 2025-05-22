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
- Preprocesa los datos y los transforma en un `NDArray`.
- Carga el modelo LSTM previamente entrenado con PyTorch (formato `.pt`).
- Realiza la predicción de consumo futuro.
- Devuelve los valores predichos como un array de `double`.

---

## 🧠 Modelo Usado

- Nombre: `lstm_model.pt`
- Tipo: LSTM
- Formato: TorchScript (`.pt`)
- Entrenado en: PyTorch + Jupyter Notebook
- Ubicación en el proyecto: `src/main/resources/model/lstm_model.pt`

---

## 🧪 Cómo se entrenó el modelo

1. Se descargó desde GitHub o Kaggle el siguiente proyecto en Jupyter: https://github.com/iamirmasoud/energy_consumption_prediction.git

```
Nombre del notebook: Energy consumption prediction using LSTM-GRU in PyTorch.ipynb
Contenía:
- Carpeta /data con CSV de consumo
- Notebook entrenando modelos LSTM y GRU
- Exportación final como lstm_model.pt y gru_model.pt
```

2. Se usaron las siguientes dependencias en Python:

```bash
pip install torch pandas matplotlib scikit-learn jupyter
```

3. Se ejecutó en local con:

```bash
jupyter notebook
```

Y se abrió el notebook donde ya venía entrenado el modelo.

4. El archivo `lstm_model.pt` fue extraído y movido al proyecto Java bajo `resources/model/`.

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
    - Predecir los próximos pasos
4. Resultado: `double[]` con los valores de consumo predicho.

---

## 📌 Notas adicionales

- El modelo fue entrenado con una forma de entrada específica, normalmente `1 x 30` (una serie temporal con 30 pasos).
- Si cambias el modelo, asegúrate de ajustar el preprocesado de datos en Java.

---

## 🧑‍💻 Contacto

Creado por Ángel Fernández Mora.  
Modelo y notebook base extraídos de proyectos públicos de aprendizaje automático.
