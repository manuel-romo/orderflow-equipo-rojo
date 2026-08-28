# Sprint 0 Evidence

## Sprint Goal
El equipo estableció y verificó un baseline reproducible del proyecto, realizando el flujo completo de clonación, compilación, pruebas y arranque. A partir de las fricciones detectadas durante ese proceso, el equipo definió una estrategia de automatización para aplicar más adelante en el proyecto.

## Repository baseline
- Repo: https://github.com/manuel-romo/orderflow-equipo-rojo
- Initial commit: [3fa325f983a284f2baaf11f65d403c927a05a386](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/3fa325f983a284f2baaf11f65d403c927a05a386)
- Fresh clone verified by: Ángel Ruiz (@Knocmare), Fernando Garcés (@fernandogarcesr) y Robert Teaze (@RobertTSL)

## Baseline execution
- Tests: El equipo ejecutó `mvn clean test` en el entorno local. Resultado: 3 pruebas ejecutadas, 2 en `orders-api`, 1 en `notifications-lambda`. 0 fallas, 0 errores.
- Artifact: El equipo generó dos artefactos mediante `mvn package`. Resultado: `orders-api-0.1.0-SNAPSHOT.jar` y `notifications-lambda-0.1.0-SNAPSHOT.jar`, con `BUILD SUCCESS`.
- Endpoint: El equipo validó `GET /actuator/health`, obteniendo `StatusCode 200`. Envió `POST /api/orders` con `{"customerId":"team-demo","total":150.00}`, obteniendo el registro creado con `id 1001` y `status CREATED`. Confirmó la persistencia con `GET /api/orders`, que devolvió el mismo registro (`id: 1001`, `customerId: team-demo`, `total: 150.00`, `status CREATED`).

## Value Stream
- AS-IS: `value-stream/as-is.md`
- TO-BE: `value-stream/to-be.md`

## Incremento demostrable
El equipo dejó la aplicación Spring Boot ejecutándose localmente a partir de un fresh clone del baseline. Cada integrante compiló con `mvn clean test` y `mvn package`, inició la aplicación con `mvn -pl orders-api spring-boot:run` y verificó que `/actuator/health` respondiera correctamente y que un registro creado por POST en `/api/orders` quedara persistido. Esta ejecución fue manual. La automatización con Maven Wrapper y pruebas `@SpringBootTest` corresponde al flujo objetivo descrito en el TO-BE y queda como trabajo pendiente.

## PR / pipeline / deployment / infraestructura
N/A — todavía no corresponde a este Sprint.

## Decisión y trade-off
Entre automatizar los prerrequisitos de entorno y automatizar las verificaciones manuales en consola, el equipo optó por lo primero. La falta de Apache Maven impidió que el equipo avanzara desde el arranque del proyecto, ya que ningún integrante podía compilar sin resolverlo antes. En cambio, los fallos de consola, como el error tipográfico al validar el POST, aparecieron ya con el entorno operativo y solo interrumpieron esa verificación. Dado que el primer problema detenía a todos y era además necesario para poder ejecutar cualquier prueba, el equipo resolvió atender el entorno primero mediante Maven Wrapper.

- *Maven Wrapper*
  - Beneficio: Estandariza la versión de Maven en todo el equipo sin que nadie tenga que instalarla a mano.
  - Costo: Implica que el líder dedique tiempo al inicio para incorporarlo y dejarlo configurado.
- *Pruebas de integración nativas*
  - Beneficio: Permite comprobar el estado del sistema de forma automática y repetible, sin depender de quién ejecute la verificación.
  - Costo: Añade tiempo de escritura de pruebas y hace la compilación local un poco más lenta.

## Demo mínima reproducible
Requiere tener instalados Java 21, Apache Maven 3.9+ y Git. Los pasos de verificación de endpoints deben ejecutarse en PowerShell, no en CMD.

- Clonar el repositorio mediante `git clone`.
- Ejecutar `mvn clean test`.
- Ejecutar `mvn package`.
- Ejecutar `mvn -pl orders-api spring-boot:run`.
- Verificar `GET /actuator/health`, esperando `StatusCode 200`.
- Enviar `POST /api/orders` con `{"customerId":"team-demo","total":150.00}` y confirmar su persistencia mediante `GET /api/orders`, esperando el registro con `id 1001` y `status CREATED`.

## Contribuciones del equipo
- Manuel Romo (@manuel-romo) - contribución verficable: Creación del repositorio oficial en GitHub, gestión de accesos al equipo y carga del commit baseline tras el proceso de bootstrap. Ejecutó `mvn clean test`, `mvn package`, `mvn -pl orders-api spring-boot:run` y validó los endpoints con `curl http://localhost:8080/actuator/health`, `Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post` y `curl http://localhost:8080/api/orders`.
- Ángel Ruiz (@Knocmare) - contribución verficable: Fresh clone del repositorio y reproducción del mismo flujo de compilación, arranque y validación de endpoints.
- Fernando Garcés (@fernandogarcesr) - contribución verficable: Fresh clone del repositorio y reproducción del mismo flujo de compilación, arranque y validación de endpoints.
- Robert Teaze (@RobertTSL) - contribución verficable: Fresh clone del repositorio y reproducción del mismo flujo de compilación, arranque y validación de endpoints.

## Mini Definition of Done
- [x] Repo y commit baseline identificables
- [x] Fresh clone verificado
- [x] Build/tests, artifact y endpoint reproducibles
- [x] AS-IS, TO-BE y Working Agreement completos
- [x] Decisión/trade-off defendible

## Retro:
- Keep: El orden de trabajo seguido por el equipo, repositorio, fresh clone, compilación, arranque y verificación de endpoints. Permitió detectar fricciones reales del proceso.
- Change: El equipo dejará de asumir que el entorno de cada integrante está listo antes de empezar a compilar y dejará de depender de verificaciones manuales por consola para validar el sistema, ya que ambas prácticas fueron la causa de la mayor parte del tiempo perdido en este Sprint.
- Next experiment: El equipo incorporará Maven Wrapper y pruebas `@SpringBootTest` como siguiente paso, y medirá si eliminan el tiempo perdido por integrante y los errores de escritura en consola.

## Uso de IA
- Herramienta: Google Gemini
- Prompt relevante: En un proyecto Java en equipo, ¿qué alternativas existen para no depender de instalar Maven manualmente en cada máquina y automatizar la verificación de endpoints que hoy se hace a mano por consola?
- Qué verificamos/cambiamos: La respuesta señaló Maven Wrapper y pruebas de integración con `@SpringBootTest`. Confirmamos su funcionamiento con búsquedas posteriores antes de considerarlos para el documento TO-BE.
