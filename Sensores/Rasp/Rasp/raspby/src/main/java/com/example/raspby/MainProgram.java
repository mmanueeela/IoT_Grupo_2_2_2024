package com.example.raspby;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainProgram {
    private static boolean stopRequested = false;
    private static final Logger logger = LoggerFactory.getLogger(MainProgram.class);

    public static void main(String[] args) {
        // Cargar configuración desde un archivo de propiedades
        Config config;
        /*try {
            config = new Config("config.properties");
        } catch (IOException e) {
            logger.error("No se pudo cargar el archivo de configuración: {}", e.getMessage());
            return;
        }*/
        MQTTHandler mqttHandler = new MQTTHandler(
                "broker.emqx.io",  // Broker MQTT
                1883,               // Puerto del broker
                null                 // Este valor lo podemos ajustar después
        );

        // Paso 2: Crear la instancia de M5StackHandler, pasando la referencia de mqttHandler
        M5StackHandler m5Handler = new M5StackHandler(mqttHandler);  // Aquí pasamos mqttHandler ya creado

        // Ahora, en el constructor de MQTTHandler, asignamos la instancia de m5Handler
        mqttHandler.setM5StackHandler(m5Handler);

        // Conectar a MQTT
        try {
            mqttHandler.connect();
            logger.info("Conexión a MQTT exitosa.");
        } catch (Exception e) {
            logger.error("Error al conectar a MQTT: {}", e.getMessage());
            return;
        }

        logger.info("Sistema iniciado. Escribe 'stop' para detener.");


        try {
            while (!stopRequested) {
                //System.out.println("funcionando");
                // Leer datos del M5Stack
                String m5Message = m5Handler.readMessage();
                if (m5Message != null && !m5Message.isEmpty()) {
                    logger.info("Mensaje recibido del M5Stack: {}", m5Message);

                    // Reenviar el mensaje al broker MQTT
                    mqttHandler.publish("m5stack/data", m5Message);

                    // Enviar respuesta al M5Stack si es necesario
                    if (m5Message.contains("config")) {
                        m5Handler.sendMessage("Configuración recibida.");
                        logger.info("Respuesta enviada al M5Stack.");
                    }
                }

                // Pausar brevemente para evitar saturación del CPU
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            logger.warn("El hilo principal fue interrumpido: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage());
        } finally {
            // Cerrar conexiones al detener
            try {
                m5Handler.close();
                mqttHandler.disconnect();
                logger.info("Conexiones cerradas correctamente.");
            } catch (Exception e) {
                logger.error("Error al cerrar recursos: {}", e.getMessage());
            }
            logger.info("Sistema detenido.");
        }


    }

    /**
     * Escucha comandos del usuario en la consola.
     */
    private static void listenForStopCommand() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (!stopRequested) {
                String input = scanner.nextLine();
                if (input != null && input.trim().equalsIgnoreCase("stop")) {
                    stopRequested=true;
                    logger.info("Solicitud de parada recibida. Deteniendo el sistema...");
                }
            }
        } catch (Exception e) {
            logger.error("Error en el hilo de escucha de comandos: {}", e.getMessage());
        }
    }
}

/**
 * Clase para cargar configuraciones desde un archivo de propiedades.
 */
class Config {
    private final Properties properties;

    public Config(String filePath) throws IOException {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
