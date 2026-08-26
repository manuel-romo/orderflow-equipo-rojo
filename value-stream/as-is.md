# Value Stream AS-IS — Sprint 0

Flujo real desde commit baseline hasta ejecución verificable del proyecto.

## 1. Trabajo
- **Creación de repositorio:** El líder de equipo creó el repositorio oficial en GitHub y añadió a los integrantes como colaboradores.
- **Carga de baseline:** El líder de equipo ejecutó el proceso de bootstrap y subió el commit baseline a la rama principal.
- **Validación de reproducibilidad:** El equipo verificó la reproducibilidad ejecutando un *fresh clone* del repositorio.
- **Compilación local:** El equipo ejecutó `mvn clean test` y `mvn package` para limpiar, probar y compilar el proyecto.
- **Arranque:** El equipo inició la aplicación localmente mediante `mvn spring-boot:run`.
- **Pruebas de conectividad:** El equipo validó la salud del sistema con `/actuator/health`, realizó un registro con POST y verificó su persistencia en `/api/orders`.

## 2. Esperas
- **Investigación e instalación del JDK:** ~10 min perdidos por un integrante del equipo en la descarga del archivo binario y configuración de variables de entorno `PATH` y `JAVA_HOME`.
- **Investigación e instalación de Maven:** ~10 min perdidos en la búsqueda del archivo binario e instalación en Windows.
- **Configuración de entorno:** ~5 min dedicados a la extracción del archivo binario y edición manual de `PATH` y `MAVEN_HOME`.
- **Cambio de cuenta en GitHub Desktop:** ~5 min perdidos investigando cómo cambiar la cuenta activa para realizar el commit baseline.

## 3. Handoffs
- **Confirmación de subida de baseline:** El líder confirmó la subida del baseline antes de solicitar la clonación al equipo.

## 4. Feedback
- **Cumplimiento de README:** Las instrucciones del archivo `README.md` del baseline fueron seguidas correctamente y permitieron iniciar el sistema.
- **Brecha técnica:** Se detectó falta de experiencia previa en el uso de consola en Windows (CMD y PowerShell) y en la configuración de cuentas en GitHub Desktop.

## 5. Fricciones observadas
- **Falla en GitHub Web:** La carga inicial del baseline falló por subida incompleta de archivos.
- **Cuenta incorrecta en GitHub Desktop:** Se subió el commit baseline con una cuenta guardada previamente debido al desconocimiento de cómo cambiar a la correcta, además de errores al intentar agregar un repositorio local sin inicializar.
- **Prerrequisitos faltantes:** Apache Maven no instalado en los entornos del equipo y falta de instalación previa del JDK en la computadora de un integrante.
- **Incompatibilidad de programa:** Se intentó ejecutar el comando `Invoke-RestMethod` en CMD en lugar de PowerShell.
- **Error tipográfico:** Escritura incorrecta del comando, `Invoked` en lugar de `Invoke`, retrasando la validación del POST.
