# 🛠️ Configuración y Pruebas del Sistema Smart Grid
🐳 Levantar Broker MQTT con 

```bash
  docker run -it --rm --name mosquitto-test \ -p 1883:1883 \ -v C:\mosquitto\mosquitto.conf:/mosquitto/config/mosquitto.conf \eclipse-mosquitto
```
---
## 🧪 Casos de Uso desde Terminal
🖥️ Comando base
```bash
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "<dispositivo>:<consumo>"
```
---
## ✅ CASO 1: Dispositivo registrado, consumo normal

```bash
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "lavadora:1500"
```
```text
    Esperado en consola de Spring Boot:
    ⚡ Mensaje recibido: lavadora:1500
    🔍 Consumo total actual: 1500.0W
```
---

## ✅ CASO 2: Dispositivo registrado, consumo supera el umbral
 - Precondición
```bash
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "tv:2500"
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "lavadora:2500"
```

 - Acción
```bash
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "horno:1200"
```
```text
  Esperado:
  
  ⚡ Mensaje recibido: horno:1200
  🔍 Consumo total actual: 6200.0W
  ⚠️ Superado umbral. Apagando 'horno'
  
  💡 El sistema detecta el total > 5000W y apaga el de mayor consumo no crítico.
```
---

## 🚫 CASO 3: Dispositivo no registrado

```bash
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "tostadora:900"
```
```text
  Esperado:
  
  ⚡ Mensaje recibido: tostadora:900
  ❌ Dispositivo desconocido 'tostadora'. Debe ser registrado antes de usar.
  
  📌 Este mensaje no se procesa ni suma al consumo total.
```
---
## ❗ CASO 4: Mensaje malformado
```bash
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "tv=2500"
```
```text
  Esperado:
  
  ❌ Formato de mensaje inválido: 'tv=2500'
  
  🔍 Se ignora porque no cumple el formato esperado nombre:consumo.
```
---

## ✅ CASO 5: Dispositivo crítico no se apaga
```bash
  mosquitto_pub -h test.mosquitto.org -t smartgrid/consumption -m "nevera:3000"
```
```text
  Esperado (si ya hay otros dispositivos activos):
  
  ⚡ Mensaje recibido: nevera:3000
  🔍 Consumo total actual: 6000.0W
  ⚠️ Superado umbral. Apagando 'tv' // Nunca apaga 'nevera'
```
---
# ⚡ Consumo Máximo Permitido: 5000W

## 🟢 Zona Baja : Terraza, Garaje, Patio

```bash
  docker run --rm eclipse-mosquitto mosquitto_pub -h 172.20.80.1 -t smartgrid/consumption -m "luz_terraza:750"
  docker run --rm eclipse-mosquitto mosquitto_pub -h 172.20.80.1 -t smartgrid/consumption -m "luz_garaje:450"
  docker run --rm eclipse-mosquitto mosquitto_pub -h 172.20.80.1 -t smartgrid/consumption -m "congelador:4100"
  docker run --rm eclipse-mosquitto mosquitto_pub -h 172.20.80.1 -t smartgrid/consumption -m "estufa:1200"
```
---
## 🟡 Zona Media: Baño, Dormitorio
```bash
  docker run --rm eclipse-mosquitto mosquitto_pub -h 172.20.80.1 -t smartgrid/consumption -m "climatizador:2200"
  docker run --rm eclipse-mosquitto mosquitto_pub -h 172.20.80.1 -t smartgrid/consumption -m "lavadora:800"
```
---
## 🔴 Zona Crítica: Cocina, Salon
```bash
  docker run --rm eclipse-mosquitto mosquitto_pub -h 172.20.80.1 -t smartgrid/consumption -m "nevera:3000"
  docker run --rm eclipse-mosquitto mosquitto_pub -h 172.20.80.1 -t smartgrid/consumption -m "router:3500"
  docker run --rm eclipse-mosquitto mosquitto_pub -h 172.20.80.1 -t smartgrid/consumption -m "horno:5200"
  docker run --rm eclipse-mosquitto mosquitto_pub -h 172.20.80.1 -t smartgrid/consumption -m "tv:980"
```

# 🧠 Motor de Decisiones

```text
El SmartGridDecisionEngine:

    - Monitorea el consumo total.

    - Apaga automáticamente dispositivos no críticos si se supera el umbral.

    - Si solo hay dispositivos críticos activos, se genera una alerta para intervención manual.

    - Permite ajustar la potencia de dispositivos críticos si es necesario y posible.
```
## 🌐 Dashboard HTML
```text
dashboard.html

   - Muestra el estado actual de los dispositivos activos.

   - Indica alerta si solo hay críticos y el consumo excede.
```
```bash
  <div th:if="${alertaCriticos}">
      🚨 ¡Atención! Todos los dispositivos activos son críticos. Requiere intervención manual.
  </div>
```
## 🌐 Gestion HTML
```text
gestion.html

    Permite:

        - Apagar dispositivos no críticos.

        - Ajustar consumo de dispositivos críticos.

        - Restringe navegación si el consumo excede el límite.
```
## 🤝 Autor

Ángel Luis Fernández Morante  
Desarrollador Backend Java  
Plexus Tech
