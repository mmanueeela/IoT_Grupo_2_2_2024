#include "WiFi.h"
#include "AsyncUDP.h"
#include <TimeLib.h>

#define BOYA_PIN 35  // Pin GPIO conectado al sensor Pico de Pato
#define Trigger 25   // Pin digital 25 para el Trigger del sensor ultrasónico
#define Echo 26      // Pin digital 26 para el Echo del sensor ultrasónico

// Configuración de red WiFi
const char* ssid = "Mari";      // Nombre de tu red WiFi
const char* password = "iloveme0807";  // Contraseña de tu red WiFi

AsyncUDP udp;

void setup() {
  Serial.begin(9600);  // Inicializar comunicación serial

  // Configuración de los pines
  pinMode(BOYA_PIN, INPUT); // Configurar el pin de la boya como entrada
  pinMode(Trigger, OUTPUT); // Configurar el pin del Trigger como salida
  pinMode(Echo, INPUT);     // Configurar el pin del Echo como entrada
  digitalWrite(Trigger, LOW); // Inicializar el pin Trigger en LOW

  // Configuración de la conexión WiFi
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
    Serial.println("CLIENTE: Error al conectar a la WiFI");
    while(1) {
      delay(1000);
    }
  }

  // Iniciamos el listener UDP en el puerto 1234
  if (udp.listen(1234)) {
    Serial.print("UDP Listening on IP: ");
    Serial.println(WiFi.localIP());

    // Recibir paquetes UDP del servidor
    udp.onPacket([](AsyncUDPPacket packet) {
      Serial.write(packet.data(), packet.length());
      Serial.println();
    });
  }
}

void loop() {
  // ---- Sensor de la Bolla (Pico de Pato) ----
  int boyaState = digitalRead(BOYA_PIN);  // Leer el estado del sensor Pico de Pato
  int boyaStatus = (boyaState == HIGH) ? 0 : 1; // 0 cuando la bolla está arriba, 1 cuando está abajo

  // ---- Sensor Ultrasónico (HC-SR04) ----
  long t;  // Tiempo que demora en llegar el eco
  long d;  // Distancia en centímetros

  digitalWrite(Trigger, HIGH);  // Enviar un pulso de 10 microsegundos
  delayMicroseconds(10);
  digitalWrite(Trigger, LOW);

  t = pulseIn(Echo, HIGH);  // Obtenemos el ancho del pulso
  if (t > 0 && t < 30000) {  // Si el tiempo es razonable
    d = t / 59;              // Calculamos la distancia en centímetros
  } else {
    d = -1;  // Error de lectura
  }

  // Filtramos y establecemos valores de la distancia
  int distanceStatus = (d > 30) ? 1 : 0;  // 1 si la distancia es mayor a 30 cm, 0 si es menor

  delay(1000);

  char texto[50];
  int BOLLA = boyaStatus;      // Cambia según el valor que desees (0 o 1)
  int DISTANCIA = distanceStatus;  // Cambia según el valor que desees (0 o 1)

  // Formateamos el texto con los valores de BOLLA y DISTANCIA
  sprintf(texto, "BOLLA:%d DISTANCIA:%d", BOLLA, DISTANCIA);

  // Enviamos el texto por la red usando broadcast en el puerto 1234
  udp.broadcastTo(texto, 1234);

  // Imprimimos en el puerto serie lo que se ha enviado
  Serial.print("CLIENTE: He enviado por la red lo siguiente: ");
  Serial.println(texto);

  // Espera 1 segundo antes de la próxima lectura
  delay(1000);
}
