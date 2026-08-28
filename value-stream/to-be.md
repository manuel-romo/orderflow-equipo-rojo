# Value Stream TO-BE — Sprint 0

Flujo objetivo desde commit baseline hasta ejecución verificable del proyecto, con automatización de integración y resolución de fricciones.

## 1. Flujo Objetivo
- **Commit baseline:** El líder sube el repositorio inicial con Maven Wrapper, incluyendo `mvnw` y `mvnw.cmd`, y una prueba de integración nativa en Java.
- **Fresh clone:** Cada integrante clona el proyecto localmente.
- **Ejecución local:** Cada integrante ejecuta `.\mvnw.cmd clean test`.
- **Verificación:** El script descarga la versión correcta de Maven, compila el código y ejecuta las pruebas automáticas.

## 2. Fricciones atendidas
- **Prerrequisitos faltantes:** La ausencia de Apache Maven generó ~15 min perdidos por integrante buscando el instalador y editando manualmente `PATH` y `MAVEN_HOME`. En el flujo objetivo, Maven Wrapper, agregado en la raíz del proyecto, descarga automáticamente la versión correcta de Maven al compilar, sin instalación manual.

  *Comprobación:* Fresh clone en una PC sin Maven, al ejecutar `.\mvnw.cmd clean test` debe descargar lo necesario y compilar sin intervención manual. La evidencia es el mensaje `BUILD SUCCESS`, sin necesidad de tener Maven preinstalado ni configurar `PATH` o `MAVEN_HOME` manualmente.

- **Errores en verificaciones manuales:** Validar a mano en consola llevó a ejecutar `Invoke-RestMethod` en CMD en lugar de PowerShell, y a un error tipográfico, `Invoked` en lugar de `Invoke`, retrasando la validación del registro de órdenes. En el flujo objetivo, estas verificaciones se reemplazan por pruebas de integración con `@SpringBootTest` que revisan automáticamente `/actuator/health` y `/api/orders` en cada compilación.

  *Comprobación:* El reporte en `/target/surefire-reports` debe mostrar, sin comandos manuales, esas pruebas ejecutadas.

Ambas comprobaciones ocurren en una sola ejecución de `.\mvnw.cmd clean test`. El mismo comando descarga Maven, compila y corre las pruebas de los endpoints, sin pasos manuales.

## 3. Priorización de automatización
Se prioriza automatizar primero los **prerrequisitos de entorno** sobre las verificaciones manuales en consola.

**Justificación:** No tener Apache Maven instalado costó ~15 min por integrante, 10 min de búsqueda e instalación y 5 min configurando `PATH` y `MAVEN_HOME`, y bloqueó a todo el equipo desde el inicio antes de poder compilar. Los errores de consola, como el tipográfico mencionado, ocurrieron después con el entorno ya funcionando, y afectaron solo un paso específico. Por su impacto mayor y porque se necesita para ejecutar las pruebas, se automatiza primero el entorno.

## 4. Trade-Offs
- **Maven Wrapper**
  - *Beneficio:* Todo el equipo compila con la misma versión de Maven, sin instalación manual.
  - *Costo:* El líder invierte tiempo inicial en agregar y configurar el wrapper.
- **Pruebas de integración nativas**
  - *Beneficio:* El estado del sistema se valida de forma repetible y automática.
  - *Costo:* Tiempo extra escribir las pruebas, y la compilación local tarda un poco más.