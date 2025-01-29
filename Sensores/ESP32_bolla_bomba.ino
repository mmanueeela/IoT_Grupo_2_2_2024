#include "WiFi.h"
#include "AsyncUDP.h"
#include <TimeLib.h>
#include "HX711.h" // Incluye la librería HX711

#define BOYA_PIN 35  // Pin GPIO conectado al sensor Pico de Pato
#define Trigger 25   // Pin digital 25 para el Trigger del sensor ultrasónico
#define Echo 26      // Pin digital 26 para el Echo del sensor ultrasónico
#define MOTOR_PIN 14 // Pin al que está conectado el motor

// Configuración del HX711
const int DOUT = 33;  // Pin DT del HX711
const int CLK = 32;   // Pin SCK del HX711
HX711 balanza;        // Crea el objeto HX711

// Configuración de red WiFi
const char* ssid = "aaaa";      // Nombre de tu red WiFi
const char* password = "12345678";  // Contraseña de tu red WiFi

AsyncUDP udp;

// Ajusta este valor después de la calibración
#define FACTOR_ESCALA -422.71871428571427  // Este es el valor de escala para la balanza

void setup() {
  Serial.begin(9600);  // Inicializar comunicación serial

  // Configuración de los pines
  pinMode(BOYA_PIN, INPUT); // Configurar el pin de la boya como entrada
  pinMode(Trigger, OUTPUT); // Configurar el pin del Trigger como salida
  pinMode(Echo, INPUT);     // Configurar el pin del Echo como entrada
  pinMode(MOTOR_PIN, OUTPUT); // Configura el pin del motor como salida
  digitalWrite(Trigger, LOW); // Inicializar el pin Trigger en LOW

  // Inicialización del sensor HX711
  balanza.begin(DOUT, CLK);
  Serial.println("No ponga ningún objeto sobre la balanza");
  Serial.println("Destarando...");
  balanza.set_scale(FACTOR_ESCALA); // Establecemos el factor de escala calculado previamente
  balanza.tare(); // El peso actual es considerado Tara
  Serial.println("Listo para pesar");

  // Configuración de la conexión WiFi
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  if (WiFi.waitForConnectResult() != WL_CONNECTED) {
    Serial.println("CLIENTE: Error al conectar a la WiFI");
    while (1) {
      delay(1000);
    }
  }

  // Iniciamos el listener UDP en el puerto 1234
  if (udp.listen(1234)) {
    Serial.print("UDP Listening on IP: ");
    Serial.println(WiFi.localIP());

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
  Serial.print("Estado de la boya: ");
  Serial.println(boyaStatus);

  // ---- Sensor Ultrasónico (HC-SR04) ----
  long t;
  long d;
  digitalWrite(Trigger, HIGH);
  delayMicroseconds(10);
  digitalWrite(Trigger, LOW);

  t = pulseIn(Echo, HIGH);
  if (t > 0 && t < 30000) {
    d = t / 59;
  } else {
    d = -1;
  }

  int distanceStatus = (d > 6) ? 1 : 0;  // 1 si la distancia es mayor a 30 cm, 0 si es menor
  Serial.print("Distancia medida: ");
  Serial.println(d);

  // Configuración del umbral de peso
  float umbralPeso = 1000;  // Cambia este valor según lo que desees

  // Control del motor basado en el peso y el estado de la boya
  float peso = balanza.get_units(20);
  Serial.print("Peso medido: ");
  Serial.print(peso);
  Serial.println(" gramos");

  if (peso > umbralPeso && boyaStatus == 1) {
    Serial.println("Motor encendido: Peso bajo y boya detectada.");
    digitalWrite(MOTOR_PIN, HIGH); // Enciende el motor
  } else {
    Serial.println("Motor apagado: Peso alto o boya no detectada.");
    digitalWrite(MOTOR_PIN, LOW);  // Apaga el motor
  }

  // Enviar datos por UDP
  char texto[50];
  sprintf(texto, "BOLLA:%d DISTANCIA:%d" , boyaStatus, distanceStatus);
  udp.broadcastTo(texto, 1234);

  Serial.print("CLIENTE: He enviado por la red lo siguiente: ");
  Serial.println(texto);

  delay(1000);
}
