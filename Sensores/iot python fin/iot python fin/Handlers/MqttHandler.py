import paho.mqtt.client as mqtt
from Handlers.M5Handler import M5SHandler
import json
import time

class MQTTHandler:
    def __init__(self, broker, port, m5):

        self.broker = broker
        self.port = port
        print(f"Inicializando MQTTHandler con broker: {broker}, puerto: {port}")

        self.client = mqtt.Client(client_id="RaspberryPiClient", callback_api_version=mqtt.CallbackAPIVersion.VERSION1) # Cliente MQTT

        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.client.on_disconnect = self.on_disconnect
        self.client.reconnect_delay_set(min_delay=5, max_delay=60)

        self.m5_handler = m5 

    def connect(self):
        """Conecta al broker MQTT.""" 
        try:
            self.client.connect(self.broker, self.port)
            self.client.loop_start()  # Inicia el loop del cliente
            print("Conectado al broker MQTT.")
        except Exception as e:
            raise RuntimeError(f"Error al conectar al broker MQTT: {e}")

    def publish(self, topic, message):
        """Publica un mensaje en un tópico MQTT.""" 
        try:
            self.client.publish(topic, message, qos=2)  # QoS 2: Exactly once
            print(f"Mensaje enviado al tópico {topic}: {message}")
        except Exception as e:
            raise RuntimeError(f"Error al publicar mensaje: {e}")

    def disconnect(self):
        """Desconecta del broker MQTT.""" 
        try:
            self.client.loop_stop()
            self.client.disconnect()
            print("Desconectado del broker MQTT.")
        except Exception as e:
            raise RuntimeError(f"Error al desconectar: {e}")

    def on_connect(self, client, userdata, flags, rc):
        """Callback para manejar la conexión.""" 
        if rc == 0:
            print("Conexión exitosa al broker MQTT.")
            # Evitar suscripciones múltiples
            if not hasattr(self, 'subscribed') or not self.subscribed:
                self.client.subscribe("pets/variables/racion", qos=1)
                self.client.subscribe("pets/variables/horas", qos=1)
                self.subscribed = True
        else:
            print(f"Conexión fallida con código {rc}")

    def on_disconnect(self, client, userdata, rc):
        """Callback para manejar la desconexión."""
        print("Conexión perdida con el broker MQTT.")
        if rc != 0:
            print("Reintentando conexión...")
            if not self.client.is_connected():
                time.sleep(5)
                try:
                    self.client.reconnect()  # Intenta reconectar automáticamente
                except Exception as e:
                    print(f"Error al intentar reconectar: {e}")

    def on_message(self, client, userdata, msg):
        """Callback para manejar la recepción de mensajes.""" 
        topic = msg.topic
        try:
            # Decodificar el payload como JSON
            payload = json.loads(msg.payload.decode('utf-8'))

            # Verificar que el campo 'msg' esté presente en el JSON
            if 'msg' not in payload:
                raise ValueError("El mensaje no contiene el campo 'msg'.")

            # Extraer el contenido de 'msg'
            contenido = payload['msg']
            print(f"Mensaje recibido en tópico {topic}: {contenido}")

            if topic == "pets/variables/racion":
                # Validar racion (número entre 1 y 5000)
                if not contenido.isdigit():
                    raise ValueError("El valor de 'racion' debe ser solo números.")
                racion_value = int(contenido)
                if racion_value <= 0 or racion_value > 5000:
                    raise ValueError("El valor de 'racion' debe ser mayor a 0 y menor o igual a 5000.")

                # Enviar mensaje por UART usando M5Handler con 'a' y el mensaje recibido
                self.m5_handler.send_message('a' + contenido)

            elif topic == "pets/variables/horas":
                # Validar horas (lista de números no mayores a 2400)
                if not all(char.isdigit() or char == ',' for char in contenido):
                    raise ValueError("El valor de 'horas' solo debe contener números y comas.")
                horas_groups = contenido.split(",")
                for group in horas_groups:
                    if int(group) > 2400 or int(group) < 0:
                        raise ValueError("Ningún grupo de 'horas' debe ser mayor a 2400 o negativo.")

                # Enviar mensaje por UART usando M5Handler con 'b' y el mensaje recibido
                self.m5_handler.send_message('b' + contenido)

            else:
                print(f"Mensaje recibido de un tópico no reconocido: {topic}")

        except json.JSONDecodeError as e:
            print(f"Error al decodificar el mensaje JSON en el tópico {topic}: {e}")
        except ValueError as e:
            print(f"Error al procesar la información del tópico {topic}: {e}")

