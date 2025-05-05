# 🧠 SmartGrid - Módulo de Incidencias ⚡

Este proyecto monitoriza dispositivos eléctricos conectados a una red inteligente. Se ha añadido una nueva funcionalidad para detectar, registrar y visualizar **incidencias energéticas**, especialmente oscilaciones de voltaje.

---

## 🔥 Nueva Funcionalidad: Incidencias

### ¿Qué es una incidencia?

Una incidencia representa una anomalía energética registrada automáticamente por el sistema, como una oscilación de voltaje.

Cada incidencia tiene:

- Fecha y hora (`fechaHora`)
- Descripción (`descripcion`)
- Severidad (`severidad`: ALTA, MEDIA, BAJA)
- Tipo de medición involucrada (`tipoMedicion`: VOLTAJE, CORRIENTE, etc.)

---

## 🧪 Cómo se genera una incidencia automáticamente

Las oscilaciones de **voltaje** fuera del rango `210V - 230V` se consideran **anómalas** y se registran automáticamente como incidencias.

Esto lo gestiona la clase `EnergyAnomalyDetector`.

```java
boolean esAnomala = energyAnomalyDetector.esOscilacionAnomala(voltaje); // true → registra incidencia
```
---

## 📥 Endpoint REST (POSTMAN)
* Obtener historial de incidencias

  - GET /incidencias

  - 📌 Devuelve todas las incidencias registradas

Ejemplo de respuesta:
```json
    [
        {
            "id": 1,
            "fechaHora": "2025-04-30T12:34:56",
            "descripcion": "Oscilación de voltaje detectada: 245.0V",
            "severidad": "MEDIA",
            "tipoMedicion": "VOLTAJE"
        }
    ]
```

* Insertar incidencia manual (para pruebas)

  - POST /incidencias

  - 📌 Crea una incidencia nueva manualmente

Body (JSON):
```json
    {
      "descripcion": "Oscilación manual detectada",
      "severidad": "BAJA",
      "tipoMedicion": "VOLTAJE"
    }
```

