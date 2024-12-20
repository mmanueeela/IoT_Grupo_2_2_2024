package com.example.raspby;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MQTTHandler implements MqttCallback {

    private IMqttClient mqttClient;
    private M5StackHandler m5;

    public MQTTHandler(String broker, int port, M5StackHandler m5) {
        try {
            String clientId = "RaspberryPiClient";
            mqttClient = new MqttClient("tcp://" + broker + ":" + port, clientId);
        } catch (MqttException e) {
            throw new RuntimeException("Error inicializando el cliente MQTT: " + e.getMessage(), e);
        }
    }

    public void connect() {
        int maxReintentos = 5;  // Número máximo de intentos
        int intentos = 0;

        while (intentos < maxReintentos) {
            try {
                MqttConnectOptions options = new MqttConnectOptions();
                options.setAutomaticReconnect(true);
                options.setCleanSession(true);
                mqttClient.connect(options);
                mqttClient.setCallback(this);
                mqttClient.subscribe("pets/variables/racion");
                mqttClient.subscribe("pets/variables/horas");
                System.out.println("Conectado al broker MQTT.");
                break;  // Si la conexión es exitosa, salimos del bucle
            } catch (MqttException e) {
                intentos++;  // Incrementamos el contador de intentos
                System.out.println("Error al conectar al broker MQTT: " + e.getMessage());
                System.out.println("Reintentando... Intento #" + intentos);

                if (intentos == maxReintentos) {
                    // Si hemos alcanzado el número máximo de intentos, mostramos un mensaje de error
                    System.out.println("Se alcanzó el número máximo de intentos para conectar.");
                    break;  // Salimos si se alcanzó el máximo de intentos
                }

                // Espera antes de intentar nuevamente (por ejemplo, 5 segundos)
                try {
                    Thread.sleep(5000);  // Pausa de 5 segundos entre intentos
                } catch (InterruptedException ie) {
                    // Si la espera es interrumpida, la excepción se maneja aquí.
                    Thread.currentThread().interrupt();
                    break;  // Salimos del bucle si la espera es interrumpida
                }
            }
        }
    }


    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            mqttMessage.setQos(2); // QoS 2: Exactly once
            mqttClient.publish(topic, mqttMessage);
            System.out.println("Mensaje enviado al topic " + topic + ": " + message);
        } catch (MqttException e) {
            throw new RuntimeException("Error al publicar mensaje: " + e.getMessage(), e);
        }
    }

    public void disconnect() {
        try {
            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
                System.out.println("Desconectado del broker MQTT.");
            }
        } catch (MqttException e) {
            throw new RuntimeException("Error al desconectar: " + e.getMessage(), e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {

        System.out.println("Conexion perdida. Reconectando...");
        connect();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        String payload = new String(message.getPayload());
        switch (topic) {
            case "pets/variables/racion":
                System.out.println("racion :"+payload);
                m5.sendMessage("a"+payload);
                break;

            case "pets/variables/horas":
                System.out.println("horas: "+payload);
                m5.sendMessage("b"+payload);
                break;

            default:
                // Si el mensaje proviene de un tópico no esperado
                //System.out.println("Mensaje recibido de un tópico no reconocido: " + topic);
                break;
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void setM5StackHandler(M5StackHandler m5Handler) {
        this.m5=m5;
    }
}
