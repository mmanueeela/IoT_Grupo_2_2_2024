#include <Arduino.h>

void setup() {
  Serial.begin(115200);            // Inicializa el puerto serie principal (monitor serie)
  Serial1.begin(115200, SERIAL_8N1, 16, 17);  // Inicializa la UART del M5Stack (ajustar pines si es necesario)
}

void loop() {
  // Verifica si hay datos disponibles en el monitor serie (Serial)
  if (Serial.available()) {
    String command = Serial.readStringUntil('\n');  // Lee el mensaje hasta encontrar un salto de línea
    command.trim();  // Elimina espacios en blanco al inicio y al final
    Serial1.println(command);  // Envía el mensaje por la UART del M5Stack
    Serial.println("Mensaje enviado: " + command);  // Muestra el mensaje en el monitor serie
  }

  // Verifica si hay datos disponibles en la UART (Serial1)
  if (Serial1.available()) {
    String incomingMessage = Serial1.readStringUntil('\n');  // Lee el mensaje hasta encontrar un salto de línea
    incomingMessage.trim();  // Elimina espacios en blanco
    Serial.println("Mensaje recibido de Serial1: " + incomingMessage);  // Muestra el mensaje en el monitor serie
  }
}
