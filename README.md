# Caso 3 - Concurrencia y Sincronización de Procesos  

## Descripción  
Este proyecto implementa un **simulador de un sistema de mensajería distribuido** en Java. El objetivo es aplicar conceptos de **concurrencia y sincronización de procesos** vistos en clase, utilizando únicamente las primitivas básicas de Java (`synchronized`, `wait`, `notify`, `notifyAll`, `join`, etc.).  

El sistema simula el envío y procesamiento de correos electrónicos entre diferentes actores:  
- **Clientes emisores**: generan correos electrónicos y los depositan en el buzón de entrada.  
- **Filtros**: consumen del buzón de entrada, determinan si un mensaje es spam o válido y lo envían a cuarentena o entrega.  
- **Manejador de cuarentena**: procesa mensajes en cuarentena durante un tiempo, descarta los maliciosos y envía los válidos al buzón de entrega.  
- **Servidores de entrega**: consumen mensajes del buzón de entrega hasta recibir un mensaje de fin.  

---

## Estructura del Proyecto  
El código se organiza en las siguientes clases:  

- **Mensaje**: representa un correo electrónico con identificador, cliente, tipo (INICIO, NORMAL, FIN), bandera de spam y tiempo de cuarentena.  
- **Buzon**: implementación de un buzón compartido con capacidad limitada (entrada y entrega) o ilimitada (cuarentena), con control de concurrencia mediante `synchronized`, `wait` y `notifyAll`.  
- **ClienteEmisor**: productor de mensajes. Envía un mensaje de inicio, N mensajes normales y un mensaje de fin.  
- **Filtro**: consumidor del buzón de entrada. Clasifica mensajes como spam o válidos, y maneja los mensajes de inicio y fin.  
- **ManejadorCuarentena**: procesa mensajes en cuarentena, reduce su tiempo de espera y decide si pasan a entrega o se descartan.  
- **ServidorEntrega**: consumidor del buzón de entrega que procesa mensajes hasta recibir un mensaje de fin.  
- **Simulador**: clase principal que inicializa los buzones, crea los hilos de clientes, filtros, manejador de cuarentena y servidores, y los ejecuta en paralelo.  

---

## Configuración  
El programa recibe parámetros de configuración desde un archivo `config.txt`.  

Ejemplo de archivo: 

clientes=3
mensajes=5
filtros=2
servidores=2
capEntrada=10
capEntrega=10

Parámetros:  
- **clientes**: número de clientes emisores.  
- **mensajes**: cantidad de mensajes generados por cada cliente.  
- **filtros**: número de filtros de spam.  
- **servidores**: número de servidores de entrega.  
- **capEntrada**: capacidad máxima del buzón de entrada.  
- **capEntrega**: capacidad máxima del buzón de entrega.  

---

## Ejecución  

### Desde un IDE (IntelliJ, VS Code, Eclipse, NetBeans)  
1. Colocar `config.txt` en el directorio raíz del proyecto.  
2. Ejecutar la clase `Simulador`.  
3. El sistema leerá la configuración y lanzará los hilos correspondientes.  

### Desde la terminal  
1. Compilar todas las clases:  
   ```bash
   javac *.java
2.	Asegurarse de que config.txt esté en el mismo directorio de ejecución.
3.	Ejecutar el simulador:
java Simulador

Funcionamiento del Sistema
1.	Clientes emisores generan mensajes y los depositan en el buzón de entrada.
2.	Filtros consumen del buzón de entrada:
	•	Mensajes de inicio → se envían al buzón de entrega.
	•	Mensajes normales válidos → se envían al buzón de entrega.
	•	Mensajes normales spam → se envían a cuarentena con un tiempo asignado.
	•	Mensajes de fin → incrementan un contador y, cuando todos los clientes terminan, se envía un mensaje de fin al buzón de entrega.
3.	Manejador de cuarentena revisa los mensajes:
	•	Cada segundo reduce su tiempo de espera.
	•	Si llega a cero, se descarta con cierta probabilidad o se pasa al buzón de entrega.
	•	Termina cuando recibe un mensaje de fin.
4.	Servidores de entrega procesan mensajes hasta recibir un mensaje de fin.

Validación

Se realizaron pruebas con diferentes configuraciones en el archivo config.txt, verificando:
•	Que todos los clientes envían sus mensajes de inicio, normales y fin.
•	Que los filtros clasifican correctamente los mensajes.
•	Que el manejador de cuarentena descarta y entrega mensajes según las reglas.
•	Que los servidores procesan hasta recibir el mensaje de fin.
•	Que todos los hilos terminan correctamente y no quedan mensajes en los buzones.