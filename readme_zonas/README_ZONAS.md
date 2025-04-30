# 🧠 Smart Grid - Gestión Inteligente de Consumo Energético

## ⚙️ ¿Qué es esta aplicación?

Smart Grid es una aplicación web diseñada para monitorear y gestionar en tiempo real el consumo eléctrico de dispositivos distribuidos en una red. Emplea comunicación MQTT para recibir datos desde sensores, y un motor de decisión interno para actuar automáticamente frente a sobreconsumos o condiciones críticas.

---

## 📡 Arquitectura General

- **Frontend:** HTML + Thymeleaf (Spring Boot MVC)
- **Backend:** Spring Boot con lógica de negocio en Java
- **Persistencia:** JPA (Base de datos con entidades como `Dispositivo`)
- **Mensajería:** Cliente MQTT (Paho) suscrito a un *broker MQTT* configurable

---

## 🔌 Funcionalidades actuales

### ✅ 1. Comunicación en tiempo real con sensores

- Suscripción a topic MQTT usando configuración externa.
- Recepción de mensajes en formato `nombre:consumo`.
- Procesamiento de datos y actualización de estado del dispositivo.

### ✅ 2. Registro y control de dispositivos

- Los dispositivos se definen en base de datos con:
    - `Nombre`
    - `Zona`
    - `Criticidad` (`CRITICA`, `MEDIA`, `BAJA`)
    - `Consumo` (valor dinámico no persistido)

- Se mantiene un **mapa en memoria** con los dispositivos activos y su consumo.

### ✅ 3. Motor de decisiones automático

- Si el **consumo total supera los 5000W**, se evalúa:
    - Se apaga (elimina del mapa) el dispositivo de menor criticidad y mayor consumo.
- Los dispositivos **críticos** no se desconectan automáticamente.

### ✅ 4. Visualización desde el navegador

- Dashboard web que lista dispositivos activos, su zona, criticidad y consumo.
- Actualización en tiempo real reflejada en la interfaz.

---

## 🚀 Nueva funcionalidad en desarrollo

### 🧠 Gestión inteligente de criticidad y zonas

#### ✔️ ¿Qué mejora?

Se añade un sistema más inteligente y realista para manejar decisiones cuando el umbral de consumo se supera, especialmente si **todos los dispositivos son críticos**.

#### 🧩 Nuevos elementos:

- **Asignación de prioridad adicional:**  
  Se incluirá un campo nuevo de `prioridad` para distinguir entre dispositivos críticos.  
  Por ejemplo: una `nevera` puede tener prioridad `1` y un `horno` prioridad `2`.

- **Sistema de alerta de decisión:**  
  Si todos los dispositivos activos son críticos y el consumo supera el límite:
    - Se genera una **alerta** visible desde el dashboard.
    - El sistema **no desconecta automáticamente** ningún dispositivo.
    - El usuario debe tomar acción desde el frontend.

- **Panel de ajustes manuales:**  
  El usuario podrá:
    - Elegir qué dispositivo desconectar.
    - Simular la reducción del voltaje en dispositivos que lo soporten.
    - Visualizar el impacto estimado de cada acción.

#### 📍 Bonus: Gestión por zonas (próximamente)

- Las zonas permitirán agrupar dispositivos por habitación o sector.
- Se podrá aplicar lógica de desconexión o priorización **por zona**.
- Ejemplo: “No cortar energía en la cocina si hay niños” → lógica basada en reglas de contexto.
---

## 🧠 Objetivo final

Crear una **inteligencia energética doméstica** capaz de:

- Tomar decisiones automáticas basadas en prioridad, consumo y contexto.
- Ofrecer control manual al usuario cuando sea necesario.
- Adaptarse a patrones y aprender (futuro: IA real).

---

## 🤝 Autor

Ángel Luis Fernández Morante  
Desarrollador Backend Java  
Plexus Tech
