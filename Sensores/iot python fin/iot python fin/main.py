from Handlers.M5Handler import M5SHandler
from Handlers.FirestoreHandler import FirestoreHandler
from Handlers.MqttHandler import MQTTHandler
from Handlers.Registro import Registro
import logging
from datetime import datetime
import time
import threading

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class MainProgram:
    def __init__(self):
        self.stop_requested = False
        self.firestore_handler = FirestoreHandler.get_instance()
        self.m5_handler = M5SHandler()
        self.mqtt_handler = MQTTHandler("broker.emqx.io", 1883, self.m5_handler)
        self.mqtt_thread = None

    def run(self):
        try:
            self.mqtt_handler.connect()
            logger.info("Conexión a MQTT exitosa.")

            logger.info("Sistema iniciado. Escribe 'stop' para detener.")

            # Iniciar hilo para escuchar mensajes UART (M5Handler)
            m5_thread = threading.Thread(target=self.listen_uart_messages, daemon=True)
            m5_thread.start()

            # Iniciar hilo para escuchar comandos 'stop'
            stop_thread = threading.Thread(target=self.listen_for_stop_command, daemon=True)
            stop_thread.start()

            while not self.stop_requested:
                
                time.sleep(1)  # Pausar brevemente para evitar saturación del CPU

        except KeyboardInterrupt:
            # Captura la interrupción por Ctrl+C
            logger.info("Interrupción por Ctrl+C detectada. Cerrando el sistema...")
            self.stop_requested = True
        except Exception as e:
            logger.error(f"Error inesperado: {e}")
        finally:
            try:
                # Cerrar el manejador de M5
                self.m5_handler.close()
                # Desconectar del broker MQTT
                self.mqtt_handler.disconnect()
                logger.info("Conexiones cerradas correctamente.")
            except Exception as e:
                logger.error(f"Error al cerrar las conexiones: {e}")

            logger.info("Sistema detenido.")

    def listen_for_stop_command(self):
        try:
            while not self.stop_requested:
                input_command = input().strip()
                if input_command.lower() == 'stop':
                    self.stop_requested = True
                    logger.info("Solicitud de parada recibida. Deteniendo el sistema...")
        except Exception as e:
            logger.error(f"Error en el hilo de escucha de comandos: {e}")

    def listen_uart_messages(self):
        try:
            while not self.stop_requested:
                m5_message = str(self.m5_handler.read_message())

                if m5_message and isinstance(m5_message, str):
                    if m5_message.startswith("MOV:"):
                        partes = m5_message.split('|')
                        print(f"partes: {partes}")  # Para depuración
                        mov = partes[0].split(":")[1] == "1"
                        uid = partes[1].split(":")[1]
                        bolla = partes[2].split(":")[1] == "1"
                        dist = partes[3].split(":")[1] == "1"
                        print("a")
                        percent = float(partes[4].split(":")[1])
                        print("b")

                        # Crear registro
                        registro = Registro(mov, bolla, dist, uid, percent)

                        # Publicar los datos en MQTT
                        self.mqtt_handler.publish("pets/sensores/movimiento", str(mov))
                        self.mqtt_handler.publish("pets/sensores/uid", uid)
                        self.mqtt_handler.publish("pets/sensores/bolla", str(bolla))
                        self.mqtt_handler.publish("pets/sensores/distancia", str(dist))
                        self.mqtt_handler.publish("pets/sensores/porcentaje", str(percent))

                        # Publicar el mensaje en Firestore
                        hora = datetime.now().strftime("%Y-%m-%d_%H:%M:%S")
                        self.firestore_handler.publish_message("Usuario1", hora, registro)

                time.sleep(0.1)  # Pausar brevemente para evitar saturación del CPU

        except Exception as e:
            logger.error(f"Error al leer mensaje desde UART: {e}")


if __name__ == "__main__":
    program = MainProgram()
    program.run()