# Avance U1 - Pipeline de calidad con GitHub Actions y SonarQube Cloud

## Identificación

**Equipo:** Equipo Rojo
**Proyecto:** OrderFlow

**Integrantes y contribuciones**
- Manuel Romo López (@manuel-romo): configuración del workflow `calidad.yaml`, métodos nuevos en `OrderService.java` para provocar el fallo del Quality Gate, pruebas unitarias que resuelven el fallo, corrección del `jacoco-maven-plugin` en `orders-api` y ajuste final de la ruta del reporte de cobertura.
- Angel Ruiz García (@Knocmare): configuración inicial de la dependencia JaCoCo en el `pom.xml` del proyecto padre.
- Fernando Garcés Rodríguez (@fernandogarcesr): apoyo en la creación de los métodos agregados y documentación.
- Robert Stewart Teaze Legleu (@RobertTSL): apoyo en la creación de pruebas unitarias y documentación.

**Repositorio:** https://github.com/manuel-romo/orderflow-equipo-rojo

**Workflow versionado:** [`.github/workflows/calidad.yaml`](https://github.com/manuel-romo/orderflow-equipo-rojo/blob/main/.github/workflows/calidad.yaml)

**Commit de entrega:** [59bb18a](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/59bb18a9377e65c55b6c7dcd37a32d2397f0c8c9) - `chore(merge): fusionar rama feature/pipeline-calidad en main`

**Pull Request:** [#1 - Feature/pipeline calidad](https://github.com/manuel-romo/orderflow-equipo-rojo/pull/1)
- Rama origen: `feature/pipeline-calidad`
- Rama destino: `main`
- Propósito del cambio: agregar el workflow de calidad `calidad.yaml` e incorporar tres métodos nuevos en `OrderService`, para cancelación de órdenes, descuentos por cupón de estudiante y consulta de órdenes por estado, junto con sus pruebas unitarias, para demostrar cómo el Quality Gate de SonarQube Cloud reacciona a código nuevo sin cobertura y cómo se corrige.
- Riesgo: bajo. El riesgo evaluado es de calidad, no de disponibilidad del servicio, y los métodos son funcionalidad aislada dentro de `OrderService` y no modifican endpoints existentes.
- Evidencia: las cuatro ejecuciones documentadas en Observación, ligadas a los commits de esta misma rama.
- Estado en GitHub: el PR ya cerrado; sus commits están integrados en `main` mediante el commit de merge `59bb18a`.

## Flujo

- **Evento:** el workflow se dispara con `push` a `main` y con `pull_request` (`opened`, `synchronize`, `reopened`) contra `main`. Se abrió un Pull Request de `feature/pipeline-calidad` a `main` para demostrar el ciclo de fallo y corrección.
- **Runner:** máquina virtual `ubuntu-latest`.
- **Steps:**
    - Descargar código de repositorio: trae el historial completo del repositorio, necesario para que SonarQube compare el código nuevo contra el código base.
    - Configurar JDK 21 (Temurin): instala el JDK que requiere el proyecto.
    - Guardar caché del escáner de SonarQube Cloud: evita volver a descargar el scanner en cada corrida.
    - Compilar, correr pruebas y analizar con SonarQube Cloud: compila el proyecto, corre las pruebas unitarias y, en la misma ejecución envía los resultados a SonarQube Cloud. Se le indica a qué proyecto y organización pertenece el análisis, se le pide esperar el resultado del Quality Gate y fallar el step si no pasa, y se le dice en qué archivo quedó el reporte de cobertura de JaCoCo. La autenticación de SonarQube Cloud y GitHub se hace con dos secrets del repositorio (`SONAR_TOKEN` y `GITHUB_TOKEN`) que no se exponen en el código ni en los logs.

**Función de Sonar:** nivel de cobertura de código nuevo en pruebas unitarias, según el perfil por defecto *Sonar way*.

## Predicción

**El proceso esperado era:**
1. Creación y configuración inicial de entorno en SonarQube Cloud.
2. Generación de key y guardado como secreto en el repositorio.
3. En una nueva rama `feature/pipeline-calidad`, creación de archivo `calidad.yaml` dentro de `.github/workflows`, con la definición del workflow.
4. Agregado métodos nuevos en `orders-api`, en la clase `OrderService.java`, sobre cancelación de órdenes, descuentos de cupón para estudiantes y consulta de órdenes por estado.
5. Creación de Pull Request con el contenido del paso 4 y comprobar que no aprueba el Quality Gate de cobertura de código nuevo (mínimo 80%).
6. Modificación de `OrderServiceTest.java` agregando pruebas unitarias para cubrir los métodos añadidos y actualizar el Pull Request; la cobertura debería superar el 80%.

**Resultados y evidencias esperadas**
- En el paso 5 se espera el error en GitHub Actions (https://github.com/manuel-romo/orderflow-equipo-rojo/actions) y en el dashboard de SonarQube Cloud (https://sonarcloud.io/summary/new_code?id=manuel-romo_orderflow-equipo-rojo&pullRequest=1).
- En el paso 6 se espera una ejecución exitosa en ambos lugares.
## Observación

- *Primera ejecución:*

Run: https://github.com/manuel-romo/orderflow-equipo-rojo/actions/runs/34905760312

Último commit anterior: [4cc83c5](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/4cc83c566a58dbaee1b8db4a576cb14ea2dc647b)

Tras añadir los nuevos métodos en `OrderService.java` (commits [2bbc34c](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/2bbc34c4ed4c2c5392208adeebdfd96dda24216e), [fdbdad9](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/fdbdad997acc506090d09c535db423fe973be038) y [4cc83c5](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/4cc83c566a58dbaee1b8db4a576cb14ea2dc647b)), el step `Compilar, correr pruebas y analizar con SonarQube Cloud` mostró, como era esperado, que no se alcanzó el 80% de cobertura sino 0%:

```bash
[ERROR] QUALITY GATE STATUS: FAILED
```

Captura: `docs/avance-u1/capturas/resumen_pr_corrida_1.png` (dashboard de SonarQube Cloud tras esta corrida).

- *Segunda ejecución:*
  Run: https://github.com/manuel-romo/orderflow-equipo-rojo/actions/runs/34906371839

Último commit anterior: [25abf85](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/25abf8546fe8215b52cfd9615ac51fc57d76863b)

Tras añadir los métodos en `OrderServiceTest.java` (commits [208e31c](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/208e31c2844fe4391104aef6c7c9b00c8d26f93b), [7b93bab](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/7b93bab1eabbbb8d85682bbfa03a58a3dbf1b195) y [25abf85](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/25abf8546fe8215b52cfd9615ac51fc57d76863b)) se esperaba superar el 80%, pero de nuevo:

```bash
[ERROR] QUALITY GATE STATUS: FAILED
```

- *Tercera ejecución:*
  Run: https://github.com/manuel-romo/orderflow-equipo-rojo/actions/runs/34907761456

Commit: [50abff8](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/50abff8b6c9a1c3620ae86f2667b3860858ce51d)

El equipo se percató de que no se había agregado la dependencia de JaCoCo en el `pom.xml` de `orders-api`, sólo estaba definida como plantilla en el `pom.xml` del proyecto padre. Al no encontrar ningún reporte de cobertura, SonarQube Cloud mostró 0%, sin pasar el Quality Gate:

```bash
[INFO] Sensor JaCoCo XML Report Importer [jacoco]
[INFO] 'sonar.coverage.jacoco.xmlReportPaths' is not defined. Using default locations: target/site/jacoco/jacoco.xml,target/site/jacoco-it/jacoco.xml,build/reports/jacoco/test/jacocoTestReport.xml
[INFO] No report imported, no coverage information will be imported by JaCoCo XML Report Importer
```

## Corrección

Con la configuración añadida (commit [50abff8](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/50abff8b6c9a1c3620ae86f2667b3860858ce51d)) se hizo la tercera corrida, esta vez con:

```bash
[INFO] QUALITY GATE STATUS: PASSED
```

Captura: `docs/avance-u1/capturas/resumen_pr_corrida_3.png` (dashboard de SonarQube Cloud tras esta corrida).

- *Cuarta ejecución:*
  Run: https://github.com/manuel-romo/orderflow-equipo-rojo/actions/runs/35157838533

Commit: [35d5123](https://github.com/manuel-romo/orderflow-equipo-rojo/commit/35d5123470283959cae62f32ee144d8b52631127)

Se añadió `-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml` al step de análisis en `.github/workflows/calidad.yaml`, para indicar explícitamente dónde queda el reporte de JaCoCo en vez de depender de que SonarQube lo adivine.

### Resultado de Sonar

Enlace: https://sonarcloud.io/dashboard?id=manuel-romo_orderflow-equipo-rojo&pullRequest=1

**Quality Gate:** Passed. 

**Hallazgos:** 8 issues nuevos abiertos (0 aceptados), 0 security hotspots nuevos, 96.7% de cobertura en *new code*, 0.0% de duplicación.

El Gate pasó cobertura (96.7% > 80% requerido), pero quedan 8 issues abiertos que el Gate por sí solo no bloquea, ver Decisión.

## Decisión

Al inicio, lo que bloqueaba la integración era que el Quality Gate de cobertura no superaba el 80% en *new code*, condición definida desde que se incluyó el análisis con *Sonar way*.

Aunque después se corrigió, condiciones como la cobertura no comprueban que los métodos y pruebas añadidos sean los adecuados; existe el riesgo de que la lógica sea incorrecta o de que las pruebas no cubran las rutas más críticas. Tampoco bloquea los 8 issues nuevos que quedaron abiertos, porque el Gate por defecto de *Sonar way* sólo condiciona por cobertura, duplicación y calificación de seguridad o mantenibilidad en *new code*, no por el número absoluto de issues.

Por eso, aun con el análisis aprobado, el equipo decidió **integrar** el cambio a `main`, ya hecho con el commit `59bb18a`, dejando pendiente de revisión en dos cosas; los 8 issues nuevos reportados por Sonar, y verificación manual que la lógica de negocio de los tres métodos nuevos sea correcta, algo que el Quality Gate no comprueba.

## Bloqueo de rama configurado

Sobre `main` se configuró una branch protection rule con: requerir Pull Request antes de fusionar, requerir aprobaciones, requerir aprobación del push más reciente sobre el PR, requerir que el status check del job `build-and-analyze` de `calidad.yaml` pase antes de fusionar, requerir que la rama esté actualizada respecto a `main` antes de fusionar, y no permitir que nadie salte estas reglas.

Esta regla se configuró después de fusionar el PR #1; a partir de ahora aplica sobre los siguientes Pull Requests del equipo.

## Trazabilidad

- Cambio de código → `2bbc34c`, `fdbdad9`, `4cc83c5` (métodos nuevos) y `208e31c`, `7b93bab`, `25abf85` (pruebas).
- Corrección de infraestructura de calidad → `50abff8` y `35d5123` (JaCoCo).
- PR → [#1](https://github.com/manuel-romo/orderflow-equipo-rojo/pull/1), `feature/pipeline-calidad` a `main`.
- Runs → los cuatro enlaces de Observación, todos disparados por `pull_request` sobre el PR #1; ese evento analiza el merge commit temporal que GitHub genera para el PR, no el HEAD de `feature/pipeline-calidad` directamente.
- Commit de entrega → `59bb18a`, que fusiona todo lo anterior en `main`.

## Limitaciones e IA

**Funciones no disponibles o pendientes en este avance:**
- Quedan 8 issues nuevos abiertos en SonarQube Cloud sin revisar.
- La regla de bloqueo de rama se configuró después de fusionar el PR #1, por lo que su cumplimiento aplicará a partir del próximo PR del equipo.
  
**Uso de IA:**
- Herramienta: Google Gemini
    - Prompt relevante: "Ejemplo de workflow de GitHub Actions con Maven para integrarlo con SonarQube Cloud."
    - Qué verificamos/cambiamos: Adaptamos el ejemplo a la estructura de nuestro proyecto, modificamos el project key y la organización, y agregamos manualmente el step de caché del scanner; confirmamos el resultado observando las ejecuciones en Actions.
- Herramienta: Google Gemini
    - Prompt relevante: "Revisar y mejorar la redacción y organización de documentos `README.md` y de `evidencia.md`."
    - Qué verificamos/cambiamos: verificamos cada afirmación técnica modificada sobre commits, runs, resultado de Sonar y estado del PR con el repositorio real antes de aceptar cambios en el texto, y corregimos el tono y fluidez generales.