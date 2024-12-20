#include "WiFi.h"
#include "AsyncUDP.h"
#include <M5Stack.h>
#include "HX711.h"

// Definición de pines
const int motorPin1 = 25;
const int motorPin2 = 26;
const int motorPin3 = 35;
const int motorPin4 = 36;

int motorSpeed = 1000;
int stepCounter = 0;
const int stepsPerRev = 2048;
const int numSteps = 8;
const int stepsLookup[8] = { 8, 12, 4, 6, 2, 3, 1, 9 };

// Variables para el sistema
int gramosRacion = 0;  // Gramos de ración
int horasDispensa[15];
int numCount = 0;                  // Array de horas de dispensa como String
bool alertaAgua = false;           // Alerta del depósito de agua
bool alertaComida = false;         // Alerta del depósito de comida
bool proximidadDetectada = false;  // Sensor de proximidad
String ultimoRFID = "UID: ---";    // Último RFID registrado
float pesoComida = 0.0;            // Peso de la comida

// Variables adicionales
char movimiento[20] = "MOVIMIENTO:0";
const int DOUT = 21;
const int CLK = 22;

HX711 balanza;
AsyncUDP udp;

const char* ssid = "aaaa";
const char* password = "12345678";

// Declaración de handles para las tareas
TaskHandle_t taskUARTHandle;
TaskHandle_t taskUDPHandle;
TaskHandle_t taskDisplayHandle;
TaskHandle_t taskSensorHandle;
TaskHandle_t taskProcessUARTHandle;

// Declaración de cola y mutex
QueueHandle_t uartQueue;
SemaphoreHandle_t xMutex;

void setup() {
  Serial.begin(115200);                       // Monitor serie
  Serial1.begin(115200, SERIAL_8N1, 16, 17);  // UART 2

  M5.begin();
  M5.Lcd.setTextSize(2);
  M5.Lcd.setTextColor(0x001F);
  M5.Lcd.setCursor(50, 100);
  M5.Lcd.println("ARRANCANDO...");
  delay(3000);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  int retryCount = 0;
  while (WiFi.waitForConnectResult() != WL_CONNECTED && retryCount < 5) {
    M5.Lcd.fillScreen(0);
    M5.Lcd.setTextColor(0xF800);
    M5.Lcd.println("ERROR al conectar a la WiFi! Reintentando...");
    Serial.println("Error de conexión WiFi, intentando nuevamente...");
    retryCount++;
    delay(1000);
  }

  if (WiFi.status() != WL_CONNECTED) {
    ESP.restart();
  }

  M5.Lcd.fillScreen(0);
  M5.Lcd.setTextColor(0x07E0);
  M5.Lcd.println("Conexion OK");
  Serial.println("Conexión WiFi establecida");
  delay(1000);

  if (udp.listen(1234)) {
    Serial.println("Escuchando en el puerto 1234...");
    udp.onPacket([](AsyncUDPPacket packet) {
      String msg = (char*)packet.data();
      // Imprimir el mensaje recibido por UDP en el serial
      Serial.println("Mensaje recibido por UDP: " + msg);
      processIncomingUDP(msg);
    });
  } else {
    Serial.println("Error al configurar el servidor UDP");
  }

  Serial.println("Iniciando balanza...");
  balanza.begin(DOUT, CLK);

  if (!balanza.is_ready()) {
    Serial.println("Error en la balanza: no está lista.");
  } else {
    Serial.println("Balanza inicializada correctamente.");
    balanza.set_scale(221000.6667);
    balanza.tare(20);
  }

  pinMode(motorPin1, OUTPUT);
  pinMode(motorPin2, OUTPUT);
  pinMode(motorPin3, OUTPUT);
  pinMode(motorPin4, OUTPUT);

  // Crear cola para UART
  uartQueue = xQueueCreate(10, sizeof(String));

  // Crear mutex para variables compartidas
  xMutex = xSemaphoreCreateMutex();

  // Crear tareas FreeRTOS
  xTaskCreate(taskUART, "TaskUART", 4096, NULL, 2, &taskUARTHandle);  // Alta prioridad
  xTaskCreate(taskProcessUART, "TaskProcessUART", 4096, NULL, 1, &taskProcessUARTHandle);
  xTaskCreate(taskUDP, "TaskUDP", 4096, NULL, 2, &taskUDPHandle);              // Alta prioridad
  xTaskCreate(taskDisplay, "TaskDisplay", 4096, NULL, 0, &taskDisplayHandle);  // Baja prioridad
  xTaskCreate(taskSensor, "TaskSensor", 4096, NULL, 1, &taskSensorHandle);


  Serial.println("Fin setup");
}

void loop() {
  // El loop queda vacío porque todas las funcionalidades están en tareas de FreeRTOS
}

// Tarea para manejar UART
void taskUART(void* pvParameters) {
  while (true) {
    if (Serial1.available()) {
      String incomingMessage = "";
      unsigned long startTime = millis();

      while (Serial1.available()) {
        char c = Serial1.read();
        incomingMessage += c;
        if (incomingMessage.length() > 256) {
          Serial.println("Mensaje UART demasiado largo. Descartado.");
          incomingMessage = "";
          break;
        }
        if (millis() - startTime > 1000) {
          Serial.println("Timeout leyendo mensaje UART.");
          incomingMessage = "";
          break;
        }
      }

      if (incomingMessage.length() > 0) {
        incomingMessage.trim();
        if (xQueueSend(uartQueue, &incomingMessage, portMAX_DELAY) != pdPASS) {
          Serial.println("Cola UART llena. Mensaje descartado.");
        }
      }
    }
    vTaskDelay(10 / portTICK_PERIOD_MS);  // Reducir la carga de CPU
  }
}

// Tarea para procesar mensajes UART desde la cola
void taskProcessUART(void* pvParameters) {
  String message;
  while (true) {
    if (xQueueReceive(uartQueue, &message, portMAX_DELAY) == pdPASS) {
      Serial.println("Mensaje recibido desde la cola: " + message);
      processIncomingUART(message);
    }
  }
}

// Tarea para manejar UDP
void taskUDP(void* pvParameters) {
  while (true) {
    // UDP es manejado por AsyncUDP, por lo que esta tarea puede permanecer inactiva
    vTaskDelay(100 / portTICK_PERIOD_MS);
  }
}

// Tarea para actualizar la pantalla
void taskDisplay(void* pvParameters) {
  while (true) {
    updateDisplay();
    vTaskDelay(1000 / portTICK_PERIOD_MS);  // Actualizar cada segundo
  }
}

// Tarea para gestionar sensores
void taskSensor(void* pvParameters) {
  while (true) {
    if (xSemaphoreTake(xMutex, portMAX_DELAY) == pdTRUE) {
      pesoComida = balanza.get_units(10);
      sendSensorData();
      xSemaphoreGive(xMutex);
    }
    vTaskDelay(5000 / portTICK_PERIOD_MS);  // Leer cada 5 segundos
  }
}

void printAllVariables() {
  if (xSemaphoreTake(xMutex, portMAX_DELAY) == pdTRUE) {
    Serial.println("------ Estado de las Variables ------");
    Serial.println("Gramos de ración: " + String(gramosRacion));
    Serial.println("Horas de dispensa: ");
    for (int i = 0; i < numCount; i++) {
      Serial.println(horasDispensa[i]);
    }
    Serial.println("Alerta Agua: " + String(alertaAgua ? "FALTA" : "OK"));
    Serial.println("Alerta Comida: " + String(alertaComida ? "FALTA" : "OK"));
    Serial.println("Proximidad Detectada: " + String(proximidadDetectada ? "SI" : "NO"));
    Serial.println("Último RFID: " + ultimoRFID);
    Serial.println("Peso de comida: " + String(pesoComida, 2) + " kg");
    Serial.println("------------------------------------");
    xSemaphoreGive(xMutex);
  }
}

// Procesar mensajes UDP entrantes
void processIncomingUDP(String msg) {
  if (xSemaphoreTake(xMutex, portMAX_DELAY) == pdTRUE) {
    // Dividir el mensaje en partes utilizando espacio como separador
    int start = 0;
    int end = msg.indexOf(' ');
    while (end != -1) {
      String subMessage = msg.substring(start, end);
      processSingleCommand(subMessage);
      start = end + 1;
      end = msg.indexOf(' ', start);
    }
    // Procesar la última parte del mensaje
    processSingleCommand(msg.substring(start));

    xSemaphoreGive(xMutex);
  }
}

// Nueva función para procesar comandos individuales
void processSingleCommand(String command) {
  if (command.startsWith("MOVIMIENTO")) {
    strncpy(movimiento, command.c_str(), sizeof(movimiento) - 1);
  } else if (command.startsWith("UID")) {
    ultimoRFID = command;
    sendSensorData();
  } else if (command.startsWith("BOLLA")) {
    alertaAgua = (command.endsWith("LLENO")) ? false : true;
  } else if (command.startsWith("DISTANCIA")) {
    alertaComida = (command.endsWith("LEJOS")) ? false : true;
  }
}


// Procesar mensajes UART entrantes
void processIncomingUART(String message) {
  if (message.length() == 0) {
    Serial.println("Mensaje vacío.");
    return;
  }

  Serial.println("Procesando mensaje UART: " + message);

  if (message.startsWith("a")) {
    int startIdx = message.indexOf(":\"") + 2;
    int endIdx = message.indexOf("\"", startIdx);
    if (startIdx == 1 || endIdx == -1) {
      Serial.println("Formato inválido para 'a'.");
      return;
    }
    if (xSemaphoreTake(xMutex, portMAX_DELAY) == pdTRUE) {
      gramosRacion = message.substring(startIdx, endIdx).toInt();
      Serial.println("Ración: " + String(gramosRacion));
      sendSensorData();
      xSemaphoreGive(xMutex);
    }

  } else if (message.startsWith("b")) {
    String numbersString = message.substring(message.indexOf(":\"") + 2);
    numbersString = numbersString.substring(0, numbersString.indexOf("\""));

    if (xSemaphoreTake(xMutex, portMAX_DELAY) == pdTRUE) {
      memset(horasDispensa, 0, sizeof(horasDispensa));
      numCount = 0;

      while (numCount < 15 && numbersString.length() > 0) {
        int endIndex = numbersString.indexOf(',');
        if (endIndex == -1) endIndex = numbersString.length();
        horasDispensa[numCount++] = numbersString.substring(0, endIndex).toInt();
        numbersString = numbersString.substring(endIndex + 1);
      }

      sendSensorData();
      for (int i = 0; i < numCount; i++) {
        Serial.println("Hora registrada: " + String(horasDispensa[i]));
      }
      xSemaphoreGive(xMutex);
    }
  } else {
    Serial.println("Tipo de mensaje no reconocido.");
  }
}

void sendSensorData() {
  if (xSemaphoreTake(xMutex, portMAX_DELAY) == pdTRUE) {
    String response = "<" + String(gramosRacion) + "," + String(alertaAgua) + "," + String(alertaComida) + "," + String(proximidadDetectada) + "," + ultimoRFID + "," + String(pesoComida, 2) + ">";
    Serial1.println(response);
    Serial.println("Datos enviados: " + response);
    xSemaphoreGive(xMutex);
  }
}

void updateDisplay() {
  if (xSemaphoreTake(xMutex, portMAX_DELAY) == pdTRUE) {
    M5.Lcd.fillScreen(0);
    M5.Lcd.setTextColor(0x07E0);
    M5.Lcd.setCursor(10, 20);
    M5.Lcd.println("Racion: " + String(gramosRacion) + "g");
    M5.Lcd.setCursor(10, 50);
    M5.Lcd.println("Agua: " + String(alertaAgua ? "FALTA" : "OK"));
    M5.Lcd.setCursor(10, 80);
    M5.Lcd.println("Comida: " + String(alertaComida ? "FALTA" : "OK"));
    M5.Lcd.setCursor(10, 110);
    M5.Lcd.println("Proximidad: " + String(proximidadDetectada ? "DETECTADO" : "NO"));
    M5.Lcd.setCursor(10, 140);
    M5.Lcd.println("RFID: " + ultimoRFID);
    M5.Lcd.setCursor(10, 170);
    M5.Lcd.println("Peso: " + String(pesoComida, 2) + " kg");
    xSemaphoreGive(xMutex);
  }
}
