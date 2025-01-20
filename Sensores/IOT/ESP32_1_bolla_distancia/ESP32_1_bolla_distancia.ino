#include "WiFi.h"
#include "AsyncUDP.h"
#include <TimeLib.h>
#include "HX711.h"  // Incluye la librería HX711

#define BOYA_PIN 35   // Pin GPIO conectado al sensor Pico de Pato
#define Trigger 25    // Pin digital 25 para el Trigger del sensor ultrasónico
#define Echo 26       // Pin digital 26 para el Echo del sensor ultrasónico
#define MOTOR_PIN 14  // Pin al que está conectado el motor

// Configuración del HX711
const int DOUT = 33;  // Pin DT del HX711
const int CLK = 32;   // Pin SCK del HX711
HX711 balanza;        // Crea el objeto HX711

// Configuración de red WiFi
const char* ssid = "aaaa";          // Nombre de tu red WiFi
const char* password = "12345678";  // Contraseña de tu red WiFi

AsyncUDP udp;

// Ajusta este valor después de la calibración
#define FACTOR_ESCALA 505.36084  // Este es el valor de escala para la balanza

void setup() {
  Serial.begin(9600);  // Inicializar comunicación serial

  // Configuración de los pines
  pinMode(BOYA_PIN, INPUT);    // Configurar el pin de la boya como entrada
  pinMode(Trigger, OUTPUT);    // Configurar el pin del Trigger como salida
  pinMode(Echo, INPUT);        // Configurar el pin del Echo como entrada
  pinMode(MOTOR_PIN, OUTPUT);  // Configura el pin del motor como salida
  digitalWrite(Trigger, LOW);  // Inicializar el pin Trigger en LOW

  // Inicialización del sensor HX711
  balanza.begin(DOUT, CLK);  // Ajusta los pines según tu configuración

  // Verificar si el sensor está listo
  if (!balanza.is_ready()) {
    Serial.println("Error: El sensor HX711 no está conectado o no responde.");
    while (1)
      ;  // Detener el programa si no se detecta el sensor
  }

  Serial.println("No ponga ningún objeto sobre la balanza");
  Serial.println("Destarando...");
  balanza.set_scale(FACTOR_ESCALA);  // Establecemos el factor de escala calculado previamente
  balanza.tare(20);                  // El peso actual es considerado Tara
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

    // Recibir paquetes UDP del servidor
    udp.onPacket([](AsyncUDPPacket packet) {
      Serial.write(packet.data(), packet.length());
      Serial.println();
    });
  }
}

void loop() {
  // ---- Sensor de la Bolla (Pico de Pato) ----
  int boyaState = digitalRead(BOYA_PIN);         // Leer el estado del sensor Pico de Pato
  int boyaStatus = (boyaState == HIGH) ? 0 : 1;  // 0 cuando la bolla está arriba, 1 cuando está abajo

  // ---- Sensor Ultrasónico (HC-SR04) ----
  long t;  // Tiempo que demora en llegar el eco
  long d;  // Distancia en centímetros

  digitalWrite(Trigger, HIGH);  // Enviar un pulso de 10 microsegundos
  delayMicroseconds(10);
  digitalWrite(Trigger, LOW);

  t = pulseIn(Echo, HIGH);   // Obtenemos el ancho del pulso
  if (t > 0 && t < 30000) {  // Si el tiempo es razonable
    d = t / 59;              // Calculamos la distancia en centímetros
  } else {
    d = -1;  // Error de lectura
  }

  // Filtramos y establecemos valores de la distancia
  int distanceStatus = (d > 30) ? 1 : 0;  // 1 si la distancia es mayor a 30 cm, 0 si es menor

  delay(1000);

  char texto[50];
  int BOLLA = boyaStatus;          // Cambia según el valor que desees (0 o 1)
  int DISTANCIA = distanceStatus;  // Cambia según el valor que desees (0 o 1)

  // Control del motor basado en el peso
  float peso = balanza.get_units(20);  // Obtiene un promedio de 20 lecturas
  Serial.println(peso);
  int MOTOR_ACTIVADO = (peso < 500) ? 1 : 0;  // Activa el motor si peso < 500
                                              // Configuramos el motor según MOTOR_ACTIVADO
  if (MOTOR_ACTIVADO == 1) {
    digitalWrite(MOTOR_PIN, HIGH);  // Motor ENCENDIDO
  } else {
    digitalWrite(MOTOR_PIN, LOW);  // Motor APAGADO
  }

  // Imprimimos en el puerto serie lo que se ha enviado
  Serial.print("MOTOR está ");
  Serial.println(MOTOR_ACTIVADO ? "ENCENDIDO" : "APAGADO");
  // Formateamos el texto con los valores de BOLLA, DISTANCIA y MOTOR
  sprintf(texto, "BOLLA:%d DISTANCIA:%d MOTOR:%d", BOLLA, DISTANCIA, MOTOR_ACTIVADO);

  // Enviamos el texto por la red usando broadcast en el puerto 1234
  udp.broadcastTo(texto, 1234);

  // Imprimimos en el puerto serie lo que se ha enviado
  Serial.print("CLIENTE: He enviado por la red lo siguiente: ");
  Serial.println(texto);

  delay(1000);
}