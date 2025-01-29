import serial
import sys

class M5SHandler:
    def __init__(self):
        self.serial = None

        try:
            # Configurar el puerto serial (ajustar el puerto según el dispositivo y sistema operativo)
            self.serial = serial.Serial('/dev/serial0', 115200, timeout=1)
            print("Conexión serial con M5Stack establecida.")

        except Exception as e:
            print(f"Error al inicializar la conexión serial: {e}")

    # Método para enviar un mensaje al M5Stack
    def send_message(self, message):
        if not message.strip():
            print("El mensaje está vacío o es nulo.")
            return

        try:
            if self.serial.is_open:
                self.serial.write((message + "\n").encode())
                print(f"Mensaje enviado al M5Stack: {message}")
            else:
                print("El puerto serial no está abierto.")
        except Exception as e:
            print(f"Error al enviar mensaje: {e}")

    # Cerrar la conexión serial
    def close(self):
        try:
            if self.serial.is_open:
                self.serial.close()
                print("Conexión serial cerrada.")
        except Exception as e:
            print(f"Error al cerrar la conexión serial: {e}")

    def read_message(self):
        try:
            if self.serial.in_waiting > 0:
                raw_data = self.serial.read(self.serial.in_waiting)  # Leer datos sin decodificar
                line = raw_data.decode('utf-8', errors='ignore').strip()  # Ignorar errores de decodificación
                print(f"Mensaje recibido (decodificado): {line}")
                return line
        except Exception as e:
            print(f"Error leyendo datos del puerto serial: {e}")
        return None


