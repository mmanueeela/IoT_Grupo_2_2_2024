#include <WiFi.h>
#include <AsyncUDP.h>
#include <M5Stack.h>
#include <Arduino.h>

// Variables para el sistema
int gramosRacion = 0;              // Variable para gramos de ración
bool alertaAgua = false;           // Alerta de agua (vacía o llena)
bool alertaComida = false;         // Alerta de comida (cerca o lejos)
bool proximidadDetectada = false;  // Alerta de proximidad (detectada o no detectada)
String ultimoRFID = "UID: ---";    // Almacena el último UID RFID
float pesoComida = 0.0;

// Variables para el sistema UDP
int boyaStatus = 0;  // 0 cuando la boya está vacía, 1 cuando está llena
int distanciaStatus = 0;  // 0 cuando la distancia es lejana, 1 cuando es cercana

// Configuración UDP
AsyncUDP udp;
const char* ssid = "aaaa";
const char* password = "12345678";

// Tareas FreeRTOS
void taskSerialRead(void *pvParameters);
void taskSerial1Read(void *pvParameters);
void taskSendSensorData(void *pvParameters);
void taskProcessUDP(void *pvParameters);

// Declaración de tareas de FreeRTOS
TaskHandle_t monitorTaskHandle;
TaskHandle_t displayTaskHandle;

void setup() {
  // Inicializa el puerto serie para depuración
  Serial.begin(115200);
  Serial.println("Iniciando setup...");

  // Inicializa el M5Stack
  M5.begin();
  Serial.println("M5Stack inicializado.");

  // Configuración de la pantalla
  M5.Lcd.setTextSize(2);       // Tamaño de texto en la pantalla
  M5.Lcd.setTextColor(WHITE);  // Color del texto

  // Conexión Wi-Fi
  Serial.println("Conectando a WiFi...");
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.waitForConnectResult() != WL_CONNECTED) {
    Serial.println("Error de conexión WiFi. Reintentando...");
    delay(1000);
  }

  Serial.println("Conexión WiFi establecida");

  // Configura el servidor UDP para escuchar en el puerto 1234
  if (udp.listen(1234)) {
    Serial.println("Escuchando en el puerto 1234...");
    udp.onPacket([](AsyncUDPPacket packet) {
      String msg = (char*)packet.data();  // Convierte los datos del paquete a String
      Serial.println("Paquete UDP recibido: " + msg); // Mensaje de depuración para el paquete recibido
      processIncomingUDP(msg);  // Procesa el mensaje recibido
    });
  } else {
    Serial.println("Error al configurar el servidor UDP");
  }

  // Crear tareas FreeRTOS después de la conexión Wi-Fi
  xTaskCreate(taskSerialRead, "Serial Read", 2048, NULL, 1, NULL);
  xTaskCreate(taskSerial1Read, "Serial1 Read", 2048, NULL, 1, NULL);
  xTaskCreate(taskSendSensorData, "Send Sensor Data", 2048, NULL, 1, NULL);
  xTaskCreate(taskProcessUDP, "Process UDP", 2048, NULL, 1, NULL);

  // Iniciar el scheduler de FreeRTOS
  vTaskStartScheduler();
}

void loop() {
  // El loop() queda vacío porque FreeRTOS gestiona las tareas
  // Si algo está fallando, esto no se ejecutará debido a que el scheduler está corriendo
  Serial.println("Dentro de loop, el scheduler FreeRTOS debería estar gestionando las tareas.");
}

// Función para leer datos del puerto serial
void taskSerialRead(void *pvParameters) {
  while (true) {
    if (Serial.available()) {
      String command = Serial.readStringUntil('\n');
      command.trim();                                  // Eliminar espacios o saltos de línea al principio y al final
      Serial1.println(command);
      Serial.println("Datos enviados desde Serial: " + command);  // Depuración
    }
    vTaskDelay(10 / portTICK_PERIOD_MS);  // Retraso para no bloquear la tarea
  }
}

// Función para leer datos del puerto serial1
void taskSerial1Read(void *pvParameters) {
  while (true) {
    if (Serial1.available()) {
      String incomingMessage = Serial1.readStringUntil('}');
      incomingMessage.trim();
      incomingMessage.replace("\n", ""); // Eliminar saltos de línea
      incomingMessage.replace(" ", "");
      Serial.println("Mensaje recibido de Serial1: " + incomingMessage);  // Depuración
    }
    vTaskDelay(10 / portTICK_PERIOD_MS);  // Retraso para no bloquear la tarea
  }
}

// Función para enviar los datos de los sensores
void taskSendSensorData(void *pvParameters) {
  while (true) {
    // Crear el mensaje con los datos de los sensores
    String response = "<" + String(gramosRacion) + "," + ultimoRFID + "," + String(alertaAgua) + "," + String(alertaComida) + "," + String(proximidadDetectada) + "," + String(pesoComida, 2) + ">";
    Serial.println("Enviando datos de sensores: " + response);  // Depuración

    // Enviar el mensaje por Serial1
    Serial1.println(response);
    Serial.println("Datos enviados por Serial1: " + response);  // Depuración

    vTaskDelay(1000 / portTICK_PERIOD_MS);  // Retraso de 1 segundo antes de enviar nuevamente
  }
}

// Función para procesar el mensaje recibido por UDP
void processIncomingUDP(String msg) {
  Serial.println("Procesando mensaje UDP...");  // Depuración
  // Procesar el mensaje y actualizar las variables correspondientes
  if (msg.startsWith("MOVIMIENTO")) {
    // Detecta movimiento: 1 es movimiento, 0 es sin movimiento
    if (msg.indexOf("1") >= 0) {
      proximidadDetectada = true;
      Serial.println("Movimiento detectado.");
    } else {
      proximidadDetectada = false;
      Serial.println("No se detectó movimiento.");
    }
  } 
  else if (msg.startsWith("UID")) {
    // Actualiza el último UID RFID
    ultimoRFID = msg;
    Serial.println("Nuevo UID RFID: " + ultimoRFID);  // Depuración
  } 
  else if (msg.startsWith("BOLLA")) {
    // Alerta de agua: valor de BOLLA es un int, 0 o 1
    int startIndex = msg.indexOf(":") + 1;  // Buscar el índice después de "BOLLA:"
    boyaStatus = msg.substring(startIndex).toInt();  // Convertir la parte numérica a entero
    alertaAgua = (boyaStatus == 1);  // Si es 1, la bolla está llena, sin alerta. Si es 0, la bolla está vacía, alerta
    Serial.println("Estado de la bolla: " + String(boyaStatus == 1 ? "Llena" : "Vacía"));  // Depuración
  } 
  else if (msg.startsWith("DISTANCIA")) {
    // Alerta de comida: valor de DISTANCIA es un int, 0 o 1
    int startIndex = msg.indexOf(":") + 1;  // Buscar el índice después de "DISTANCIA:"
    distanciaStatus = msg.substring(startIndex).toInt();  // Convertir la parte numérica a entero
    alertaComida = (distanciaStatus == 1);  // Si es 1, la comida está cerca, alerta. Si es 0, la comida está lejos, sin alerta
    Serial.println("Estado de la distancia: " + String(distanciaStatus == 1 ? "Cerca" : "Lejos"));  // Depuración
  }

  // Mostrar los datos en el puerto serie para depuración
  Serial.println("MOVIMIENTO: " + String(proximidadDetectada) + 
                 ", UID: " + ultimoRFID + 
                 ", BOLLA: " + (alertaAgua ? "Vacía" : "Llena") + 
                 ", DISTANCIA: " + (alertaComida ? "Cerca" : "Lejos"));

  // Aquí puedes actualizar la pantalla del M5Stack si es necesario
  M5.Lcd.clear();  // Limpiar pantalla antes de mostrar los nuevos datos
  M5.Lcd.setCursor(0, 0);
  M5.Lcd.print("Movimiento: ");
  M5.Lcd.println(proximidadDetectada ? "Detectado" : "No Detectado");
  M5.Lcd.print("UID: ");
  M5.Lcd.println(ultimoRFID);
  M5.Lcd.print("Bolla: ");
  M5.Lcd.println(alertaAgua ? "Vacía" : "Llena");
  M5.Lcd.print("Distancia: ");
  M5.Lcd.println(alertaComida ? "Cerca" : "Lejos");
}

// Tarea adicional para procesar UDP y actualizar estado
void taskProcessUDP(void *pvParameters) {
  while (true) {
    // Las funciones de procesamiento UDP están manejadas por la función 'processIncomingUDP' dentro de la tarea 'udp.onPacket'
    vTaskDelay(10 / portTICK_PERIOD_MS);  // Retraso para evitar bloqueo
  }
}
