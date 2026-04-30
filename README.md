# Propuesta de Arquitectura para Smart Grid con Java y Spring Boot

## ✨ Resumen Ejecutivo
Este documento presenta una propuesta de arquitectura para el desarrollo de un sistema Smart Grid enfocado en la gestión de iluminación, recolección de datos y optimización basada en inteligencia artificial. La tecnología principal es Java con Spring Boot, y se han elegido herramientas y patrones que permiten escalabilidad, mantenibilidad y facilidad de desarrollo.

## 🧠 Tecnologías y Herramientas Seleccionadas

| Componente                | Tecnología / Herramienta              | Justificación |
|---------------------------|----------------------------------------|----------------|
| Lenguaje                 | Java                                   | Conocimiento previo y robustez |
| Framework                | Spring Boot                            | Para microservicios |
| Contenedores             | Docker                                 | Despliegue y portabilidad |
| Orquestador              | Kubernetes (AWS EKS)                   | Escalabilidad (futuro) |
| Plataforma Cloud         | AWS (ECS, RDS, S3, etc.)               | Infraestructura escalable |
| Seguridad                | Spring Security + JWT                  | Autenticación sencilla sin Keycloak |
| Mensajería               | Spring Kafka                           | Procesamiento asíncrono de eventos |
| Base de datos            | PostgreSQL (TimescaleDB opcional)     | Familiaridad y soporte de series temporales |
| Cache                    | Redis                                  | Para rendimiento y rapidez |
| IA                       | DL4J (DeepLearning4J)                  | Soporte en Java y comunidad activa |
| Documentación API        | Swagger / OpenAPI                      | Para documentar servicios REST |
| CI/CD                    | Git + GitFlow + Jenkins                | Automatización y control de versiones |

## 🔄 Arquitectura del Sistema

Se propone una arquitectura de **Microservicios con Hexagonal Architecture** (puertos y adaptadores). Cada microservicio será independiente, desacoplado, y seguirá principios SOLID y buenas prácticas.

### Diagrama General (descriptivo):

- Sensor Service → Kafka → Data Processing Service
- Data Processing → PostgreSQL / Redis
- Optimization Service (IA) → expone sugerencias
- Control API Gateway (con JWT) → coordina todo + acceso frontend

### Microservicios

1. **Sensor Service**
   - Recoge y publica eventos de sensores (Kafka).
   - Validación básica y forward.

2. **Data Processing Service**
   - Procesa eventos de Kafka, almacena en PostgreSQL.
   - Accede a Redis para consultas rápidas.

3. **Optimization (IA) Service**
   - DL4J para inferencia y recomendaciones.
   - Expone endpoints REST para sugerencias de eficiencia.

4. **Control API Gateway**
   - Gestiona accesos.
   - Expone la API documentada con Swagger.
   - JWT para autenticación.

## 🧰 Patrón Arquitectónico y de Diseño

### Arquitectura:
- **Microservicios** + **Hexagonal**: alta cohesión, bajo acoplamiento.

### Patrones de Diseño:
| Patrón     | Uso |
|------------|-----|
| Singleton  | Servicios comunes como Logger, Config, etc. |
| Factory    | Creación de objetos como sensores u optimizadores |
| Strategy   | Algoritmos de optimización distintos |
| Observer   | Integración eventos Kafka → escucha servicios |


## 📖 Metodología de Desarrollo

- **TDD (Test Driven Development)**: para garantizar fiabilidad y facilitar refactorizaciones.
- **GitFlow** para el control de versiones.
- **CI/CD con Jenkins**: para despliegues automáticos y testing.

## 📊 Base de Datos

- **PostgreSQL** como base principal relacional.
- **Redis** como cache para respuestas rápidas.
- Posibilidad de usar TimescaleDB si se necesitan series temporales.
- Se omite uso de gestores como Liquibase o Flyway de momento (poco necesario en esta fase).

## 🤖 Seguridad

- JWT + Spring Security para autenticación en la API Gateway.
- OAuth2 podría evaluarse en el futuro si se decide integrar Keycloak.

## 🚀 CI/CD y DevOps

- Docker para empaquetado.
- Jenkins para CI/CD.
- AWS para infraestructura y despliegue.
- Kubernetes (a considerar más adelante) para orquestación y escalado.

## 🌐 Documentación y API

- Swagger + OpenAPI para documentar los servicios.
- Acceso vía API Gateway controlado por JWT.

## 📈 Monitorización y Observabilidad

| Herramienta         | Uso Principal                                 |
|---------------------|------------------------------------------------|
| **ElasticSearch**   | Almacén de logs estructurados                 |
| **Kibana**          | Dashboard visual para logs y alertas         |
| **Logstash** / Filebeat | Ingesta de logs desde microservicios         |
| **Prometheus**      | Monitorización de métricas técnicas          |
| **Grafana**         | Visualización de métricas (CPU, RAM, tráfico)|

- Spring Boot puede incluir micrométricas expuestas vía `/actuator/prometheus`
- Se recomienda incluir alertas por consumo excesivo, errores y tiempos de respuesta.

## 🚀 Mini-Doc: Tecnologías escalables a futuro que aún no se implementan

| **Tecnología**                         | **Rol futuro**                             | **¿Por qué no ahora?**                                                                 |
|----------------------------------------|---------------------------------------------|----------------------------------------------------------------------------------------|
| **Kubernetes**                         | Orquestación de contenedores               | Requiere curva de aprendizaje fuerte, ideal si el proyecto crece. De momento con Docker está bien. |
| **Keycloak / OAuth2**                  | Gestión de identidades centralizada        | Con JWT por ahora. Omitir la complejidad que se puede evitar.                                 |
| **Liquibase / Flyway**                 | Control de versiones de base de datos      | Es potente, pero no necesario en MVP. Se puede aplicar más adelante si hay muchos cambios en DB. |
| **Grafana + Prometheus**               | Monitorización a gran escala               | Si no hay muchos servicios aún, basta con logs simples o Actuator. Escalable después.  |
| **Kafka Streams / KSQL**               | Procesamiento de eventos en tiempo real    | Kafka con consumidores simples para arrancar. Streams es más potente pero complejo. |
| **CI/CD avanzado con GitHub Actions o GitLab CI** | Integraciones modernas cloud-native        | Jenkins está bien, Se puede migrar a soluciones cloud más fáciles luego.            |
| **Terraform / Pulumi**                 | Infraestructura como código                | Por ahora se puede crear los recursos a mano o desde consola de AWS. Esto ayudará a escalar luego. |


## ⚠️ Consideraciones Finales

- Se prioriza simplicidad, escalabilidad y lo que se pueda mantener hoy.
- Todo lo que no se domine se deja opcional para versiones futuras.
- El MVP puede centrarse en: recolección de datos + visualización básica + control manual.
- La IA se puede simular inicialmente con reglas simples, y luego evolucionar con DL4J.

---

**Esta arquitectura está pensada para ser defendible, realizable y extensible.** Si hay cambios en el futuro, permite adaptar fácilmente la estructura sin reescribir todo.

❌ NO SE INCLUIRÁ PYTHON ❌ 

## 🤝 Autor

Ángel Luis Fernández Morante  
Desarrollador Backend Java  
