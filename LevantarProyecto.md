# 💡 SmartGrid Monitor - Simulación con MQTT y Java Spring Boot

Este proyecto simula una red **Smart Grid doméstica**, donde distintos sensores (electrodomésticos) publican su consumo eléctrico y el sistema toma decisiones automáticas si se supera un umbral definido.

---

## 🔧 Requisitos Previos

- Java 17
- Maven
- IntelliJ IDEA (Community Edition sirve)
- Mosquitto (Cliente MQTT para publicar mensajes)
- Navegador web (para ver el dashboard)

---

## 📦 Instalación de Mosquitto en Windows (solo cliente)

1. Descarga el cliente oficial:  
   👉 https://mosquitto.org/download/

2. Extrae el `.zip` y accede a la carpeta donde están los ejecutables:
   ```
   mosquitto_pub.exe
   mosquitto_sub.exe
   ```

3. Añade la ruta a tu `PATH` o navega hasta ella en la terminal.

Para probarlo, ejecuta:

```bash
  mosquitto_pub -h test.mosquitto.org -t test/topic -m "Hola MQTT"
```

---

## 🚀 Cómo ejecutar la aplicación

1. Abre el proyecto en IntelliJ.
2. Ejecuta la clase principal:

```
MqttmonitorApplication.java
```

Esto arrancará Spring Boot en el puerto **8080** y se suscribirá al broker MQTT público:  
`tcp://test.mosquitto.org:1883`

---

## 📤 Simular sensores desde la terminal

Desde tu terminal con Mosquitto:

```bash
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "tv:1500"
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "lavadora:2500"
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "lavavajillas:2000"
```

---

## 📊 ¿Qué pasa internamente?

- Cada mensaje se recibe por MQTT.
- Se parsea con formato `dispositivo:consumo` (ej: `tv:1500`)
- Si el consumo total supera **5000W**, el sistema "apaga" automáticamente el dispositivo que más consume.
- Verás logs como:

```log
⚡ Mensaje recibido: tv:1500
🔍 Consumo total actual: 1500.0W
⚡ Mensaje recibido: lavadora:2500
🔍 Consumo total actual: 4000.0W
⚡ Mensaje recibido: lavavajillas:2000
🔍 Consumo total actual: 6000.0W
⚠️ Superado el umbral. Apagando 'lavavajillas'
```

---

## 🌐 Visualización Web

Abre tu navegador y accede a:

```
http://localhost:8080
```

Verás una tabla con los dispositivos activos y su consumo actualizado dinámicamente.

---

## 📦 Estructura del proyecto

```
├— controller/               ← Thymeleaf Controller
├— logic/                   ← IA simulada que gestiona decisiones
├— service/                 ← MQTT Subscriber Service
├— config/                  ← Spring Beans
├— templates/dashboard.html← Interfaz visual
└— tests/                   ← Tests con JUnit y Mockito
```

---

## 🧪 Tests

- Cubiertos con JUnit 5 y Mockito.
- Ejecutables desde IntelliJ con "Run with Coverage".

---

## 🛠️ ¿Qué puedo hacer después?

Si se da el visto bueno, estas son extensiones lógicas:

- ✅ Exponer un API REST con Swagger (OpenAPI)
- 📈 Persistir históricos en PostgreSQL o InfluxDB
- ♻️ Sustituir MQTT por otro middleware local/privado
- 🤖 Sustituir IA simulada por DL4J (DeepLearning4J)

---

## 🤝 Autor

Ángel Luis Fernández Morante  
Desarrollador Backend Java  
Plexus Tech

