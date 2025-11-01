# Caso 3 - Concurrencia y Sincronización de Procesos  

## Descripción  
Este proyecto implementa un **simulador de un sistema de mensajería distribuido** en Java, diseñado para aplicar los conceptos de **concurrencia y sincronización de procesos** vistos en clase.  

El sistema utiliza únicamente las primitivas básicas de sincronización de Java (`synchronized`, `wait`, `notify`, `notifyAll`, `join`) y coordina múltiples hilos que representan el flujo completo de procesamiento de correos electrónicos en un entorno distribuido.  

El objetivo principal es demostrar el uso correcto de la **comunicación entre hilos productores y consumidores**, evitando bloqueos, condiciones de carrera e interbloqueos, garantizando además una **finalización ordenada y automática del sistema**.  

---

## Arquitectura del Sistema  

El sistema está compuesto por los siguientes actores concurrentes:  

- **Clientes emisores**: generan mensajes (inicio, normales, fin) y los depositan en el buzón de entrada.  
- **Filtros de spam**: consumen del buzón de entrada, clasifican los mensajes como spam o válidos, y los dirigen a cuarentena o entrega.  
- **Manejador de cuarentena**: procesa los mensajes en cuarentena durante cierto tiempo, descarta los maliciosos y libera los válidos al buzón de entrega.  
- **Servidores de entrega**: consumen mensajes del buzón de entrega y los procesan hasta recibir una señal de fin global.  
- **Coordinador de finalización global**: detecta cuando todos los clientes han finalizado y emite señales de cierre (`FIN`) para que todos los hilos terminen correctamente.  

---

## Estructura del Proyecto  

El proyecto se compone de las siguientes clases:

| Clase | Descripción |
|-------|--------------|
| **Mensaje** | Representa un correo electrónico con identificador, cliente emisor, tipo (`INICIO`, `NORMAL`, `FIN`), bandera de spam y tiempo de cuarentena. |
| **Buzon** | Implementa una cola compartida con capacidad limitada (entrada y entrega) o ilimitada (cuarentena). Gestiona la sincronización de productores y consumidores mediante `synchronized`, `wait` y `notifyAll`. |
| **ClienteEmisor** | Actúa como productor. Envía un mensaje de inicio, varios mensajes normales (algunos spam) y un mensaje final de cierre (`FIN`). |
| **FiltroSpam** | Actúa como consumidor del buzón de entrada. Clasifica los mensajes y coordina el fin del sistema mediante un **Coordinador de Fin Global** compartido entre todos los filtros. |
| **ManejadorCuarentena** | Procesa mensajes spam con un tiempo de cuarentena aleatorio (10–20 segundos). Cada segundo reduce su tiempo restante y, cuando llega a cero, puede descartar el mensaje o enviarlo a entrega. |
| **ServidorEntrega** | Consume del buzón de entrega, simula la entrega de los mensajes válidos y finaliza al recibir un mensaje de tipo `FIN`. |
| **Simulador** | Clase principal. Crea los buzones, inicializa los hilos de todos los actores y coordina su ejecución y finalización automática. |

---

## Mecanismo de Sincronización y Finalización  

El sistema utiliza **sincronización explícita** (`wait`, `notifyAll`) y un mecanismo de coordinación global implementado en la clase `FiltroSpam.CoordinadorFin`:

1. Cada filtro aumenta un contador global cada vez que recibe un mensaje `FIN` de un cliente.  
2. Cuando el número de `FIN` recibidos es igual al total de clientes, **un único filtro** emite un conjunto de mensajes `FIN` globales:  
   - Uno por cada servidor de entrega.  
   - Uno para el manejador de cuarentena.  
   - Uno para cada filtro restante (para desbloquear los que estén esperando en `take()`).  
3. Cada actor, al recibir su `FIN`, sale limpiamente del ciclo principal.  
4. El hilo principal (`Simulador`) hace `join()` sobre todos los hilos y muestra el mensaje:  

=== Simulación completada. Todos los hilos finalizaron correctamente. ===

De esta forma se garantiza que **ningún hilo quede bloqueado** y que la simulación se detenga automáticamente sin intervención manual.

---

## Configuración  

El programa toma los parámetros desde un archivo `config.txt` ubicado en el mismo directorio del proyecto.  

Ejemplo de configuración:

clientes=3
mensajes=5
filtros=2
servidores=2
capEntrada=10
capEntrega=10

**Parámetros:**
- `clientes`: número de clientes emisores.  
- `mensajes`: cantidad de mensajes que genera cada cliente.  
- `filtros`: número de filtros de spam concurrentes.  
- `servidores`: número de servidores de entrega concurrentes.  
- `capEntrada`: capacidad máxima del buzón de entrada.  
- `capEntrega`: capacidad máxima del buzón de entrega.  

---

## Ejecución  

### Desde un IDE (IntelliJ, VS Code, Eclipse, NetBeans)
1. Colocar `config.txt` en el directorio raíz del proyecto.  
2. Ejecutar la clase `Simulador`.  
3. Observar el flujo de ejecución y los mensajes en la consola.

### Desde la terminal
```bash
javac *.java
java Simulador