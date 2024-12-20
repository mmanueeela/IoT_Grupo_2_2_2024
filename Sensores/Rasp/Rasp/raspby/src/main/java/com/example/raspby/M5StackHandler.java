package com.example.raspby;

import com.pi4j.io.serial.*;
import com.pi4j.context.Context;
import com.pi4j.Pi4J;
import com.pi4j.util.Console;

public class M5StackHandler {
    private Serial serial;
    private Console console;  // Utilizamos Console para la salida
    private  MQTTHandler mqtt;

    public M5StackHandler(MQTTHandler mqtt) {
        this.mqtt=mqtt;
        int maxRetries = 5;  // Número máximo de reintentos
        int attempt = 0;  // Intento actual
        boolean connected = false;

        // Creamos el objeto 'Console' para mostrar mensajes
        console = new Console();

        while (attempt < maxRetries && !connected) {
            try {
                // Configurar y abrir el puerto serial con Pi4J
                Context pi4j = Pi4J.newAutoContext();
                serial = pi4j.create(Serial.newConfigBuilder(pi4j)
                        .use_115200_N81()
                        .dataBits_8()
                        .parity(Parity.NONE)
                        .stopBits(StopBits._1)
                        .flowControl(FlowControl.NONE)
                        .id("my-serial")
                        .device("/dev/serial0") // Usar el puerto proporcionado, por ejemplo "/dev/ttyS0"
                        .provider("pigpio-serial")  // Especificar el proveedor correcto
                        .build());

                // Intentar abrir el puerto serial
                serial.open();

                // Esperar hasta que el puerto serial esté abierto
                console.println("Esperando hasta que el puerto serial esté abierto...");
                while (!serial.isOpen()) {
                    Thread.sleep(250);  // Esperar 250 ms antes de verificar nuevamente
                }

                // Confirmamos que el puerto está abierto
                console.println("Puerto serial abierto exitosamente.");
                connected = true;  // Si la conexión se establece, salimos del bucle;
                console.println("Conexión serial con M5Stack establecida.");

            } catch (Exception e) {
                attempt++;
                console.println("Error al inicializar la conexión serial: " + e.getMessage());
                e.printStackTrace();
                if (attempt < maxRetries) {
                    console.println("Reintentando... (" + attempt + "/" + maxRetries + ")");
                    try {
                        Thread.sleep(2000);  // Esperar 2 segundos antes de reintentar
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();  // Restablecer el estado de interrupción
                    }
                } else {
                    console.println("Número máximo de reintentos alcanzado. No se pudo establecer la conexión.");
                }
            }
        }
    }

    // Método para enviar un mensaje al M5Stack
    public void sendMessage(String message) {
        console.println("Enviando mensaje: " + message); // Verifica si el mensaje es correcto

        if (message == null || message.trim().isEmpty()) {
            console.println("El mensaje está vacío o es nulo.");
            return;
        }

        try {
            if (serial.isOpen()) {
                serial.write(message + "\n");
                console.println("Mensaje enviado al M5Stack: " + message);
            } else {
                console.println("El puerto serial no está abierto.");
            }
        } catch (Exception e) {
            console.println("Error al enviar mensaje: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }

    // Cerrar la conexión serial
    public void close() {
        try {
            if (serial.isOpen()) {
                serial.close();
                console.println("Conexión serial cerrada.");
            }
        } catch (Exception e) {
            console.println("Error al cerrar la conexión serial: " + e.getMessage());
        }
    }

    // Leer mensaje del puerto serial
    public String readMessage() {
        StringBuilder lineBuilder = new StringBuilder();
        try {
            int available = serial.available();
            if (available > 0) {
                for (int i = 0; i < available; i++) {
                    byte b = serial.readByte();
                    if (b < 32) { // Fin de mensaje (por ejemplo, si se recibe un carácter de control)
                        if (lineBuilder.length() > 0) {
                            String line = lineBuilder.toString();
                            console.println("Recibido de M5: " + line);
                            // Procesar el mensaje según el comando recibido
                            line = cleanAndFormatLine(line);
                            processCommand(line);
                            return line;
                        }
                    } else {
                        lineBuilder.append((char) b);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            console.println("Error leyendo datos del puerto serial: " + e.getMessage());
        }
        return null;
    }
    private String cleanAndFormatLine(String line) {
        // Eliminar saltos de línea, espacios adicionales y convertir todo a minúsculas
        line = line.replaceAll("[\\n\\r]+", "");  // Elimina saltos de línea
        line = line.replaceAll("\\s+", "");        // Elimina todos los espacios
        line = line.toLowerCase();                 // Convierte a minúsculas
        return line;
    }
    private void processCommand(String line) {
        // Dividimos el mensaje en varios comandos usando ";" como delimitador
        String[] commands = line.split(";");

        // Procesamos cada comando individualmente
        for (String command : commands) {
            // Separamos el comando del valor utilizando "=" como delimitador
            String[] parts = command.split("=");

            if (parts.length == 2) {
                String key = parts[0].trim();  // El nombre del comando
                String value = parts[1].trim(); // El valor del comando

                // Procesamos cada comando con su valor asociado
                switch (key) {
                    case "boya":
                        mqtt.publish("pets/sensores/boya",value);
                        console.println("publicado en pets/sensores/boya: "+value);
                        break;

                    case "distancia":
                        mqtt.publish("pets/sensores/distancia", value);  // Publica en el topic "pets/sensores/distancia"
                        console.println("Publicado en pets/sensores/distancia: " + value);
                        break;

                    case "movimiento":
                        mqtt.publish("pets/sensores/movimiento", value);  // Publica en el topic "pets/sensores/movimiento"
                        console.println("Publicado en pets/sensores/movimiento: " + value);
                        break;

                    case "peso":
                        mqtt.publish("pets/sensores/peso", value);  // Publica en el topic "pets/sensores/peso"
                        console.println("Publicado en pets/sensores/peso: " + value);
                        break;

                    case "rfid":
                        mqtt.publish("pets/sensores/rfid", value);  // Publica en el topic "pets/sensores/rfid"
                        console.println("Publicado en pets/sensores/rfid: " + value);
                        break;

                    case "proximidad":
                        mqtt.publish("pets/sensores/proximidad", value);  // Publica en el topic "pets/sensores/proximidad"
                        console.println("Publicado en pets/sensores/proximidad: " + value);
                        break;

                    default:
                        console.println("Comando no reconocido: " + key);
                        break;
                }
            } else {
                console.println("Comando mal formado: " + command);
            }
        }
    }
}
