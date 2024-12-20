#include <WiFi.h>
#include <AsyncUDP.h>
#include <M5Stack.h>

// Variables para el sistema
int gramosRacion = 0;              // Variable para gramos de ración
bool alertaAgua = false;           // Alerta de agua (vacía o llena)
bool alertaComida = false;         // Alerta de comida (cerca o lejos)
bool proximidadDetectada = false;  // Alerta de proximidad (detectada o no detectada)
String ultimoRFID = "UID: ---";    // Almacena el último UID RFID

int boyaStatus = 0;  // 0 cuando la boya está vacía, 1 cuando está llena
int distanciaStatus = 0;  // 0 cuando la distancia es lejana, 1 cuando es cercana

// Configuración UDP
AsyncUDP udp;
const char* ssid = "aaaa";
const char* password = "12345678";

// Tareas para manejar el monitoreo y la pantalla
TaskHandle_t monitorTaskHandle;
TaskHandle_t displayTaskHandle;

void setup() {
  Serial.begin(115200);  // Inicializa el puerto serie a 115200 baudios
  M5.begin();            // Inicializa el M5Stack

  // Configuración de la pantalla
  M5.Lcd.setTextSize(2);       // Tamaño de texto en la pantalla
  M5.Lcd.setTextColor(WHITE);  // Color del texto

  // Configura WiFi
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
      // Serial.println("Mensaje recibido por UDP: " + msg);  // Imprime el mensaje en el puerto serie
      processIncomingUDP(msg);  // Procesa el mensaje recibido
    });
  } else {
    Serial.println("Error al configurar el servidor UDP");
  }

  // Crear las tareas de monitoreo y visualización (si es necesario)
  // xTaskCreate(monitorTask, "Monitor Task", 1024, NULL, 1, &monitorTaskHandle);
  // xTaskCreate(displayTask, "Display Task", 1024, NULL, 1, &displayTaskHandle);
}

void loop() {
  // El loop puede quedarse vacío, las tareas están manejando el flujo de trabajo
  // Si no usas tareas, puede que desees actualizar la pantalla aquí o hacer algún otro trabajo
}

// Función para procesar el mensaje recibido por UDP
void processIncomingUDP(String msg) {
  // Procesar el mensaje y actualizar las variables correspondientes
  if (msg.startsWith("MOVIMIENTO")) {
    // Detecta movimiento: 1 es movimiento, 0 es sin movimiento
    if (msg.indexOf("1") >= 0) {
      proximidadDetectada = true;
    } else {
      proximidadDetectada = false;
    }
  } 
  else if (msg.startsWith("UID")) {
    // Actualiza el último UID RFID
    ultimoRFID = msg;         
  } 
  else if (msg.startsWith("BOLLA")) {
    // Alerta de agua: valor de BOLLA es un int, 0 o 1
    int startIndex = msg.indexOf(":") + 1;  // Buscar el índice después de "BOLLA:"
    boyaStatus = msg.substring(startIndex).toInt();  // Convertir la parte numérica a entero
    alertaAgua = (boyaStatus == 1);  // Si es 1, la bolla está llena, sin alerta. Si es 0, la bolla está vacía, alerta
  } 
  else if (msg.startsWith("DISTANCIA")) {
    // Alerta de comida: valor de DISTANCIA es un int, 0 o 1
    int startIndex = msg.indexOf(":") + 1;  // Buscar el índice después de "DISTANCIA:"
    distanciaStatus = msg.substring(startIndex).toInt();  // Convertir la parte numérica a entero
    alertaComida = (distanciaStatus == 1);  // Si es 1, la comida está cerca, alerta. Si es 0, la comida está lejos, sin alerta
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
