#include <Arduino.h>
#include <WiFi.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include "HX711.h"
#include <M5Stack.h>


// Pines del motor
#define clkPin 2  // Pin para pulsos (CLK)
// Pines del sensor HX711
#define DOUT 21  // Pin de datos
#define CLK 22   // Pin de reloj

HX711 balanza;

// Peso conocido para la calibración
const float pesoConocido = 500.0;  // Peso en gramos

// Variables globales
bool percent = false;
bool motorEncendido = false;     // Estado del motor
unsigned long lastStepTime = 0;  // Tiempo del último pulso
unsigned long stepInterval = 1;  // Intervalo entre pulsos en microsegundos (inicia con 1500)

int ultimoMinuto = -1;
bool porcentajePendiente = true;
bool mensajeMostrado = false;

bool proximidadDetectada = false;
String ultimoRFID = "123456ABC";
bool alertaDeBoya = false;
bool alertaDeComida = false;
float porcentajeComida = 0.0;

int racion = 600;
String listaHoras = "0000";

bool primeraVezAlimentar = true;
bool dataChanged = false;

// Configuración Wi-Fi
const char* ssid = "aaaa";
const char* password = "12345678";

// Configuración NTP
WiFiUDP udp;
NTPClient timeClient(udp, "pool.ntp.org", 3600, 3600);


// Configuración UDP
WiFiUDP udpServer;
const int localUdpPort = 1234;  // Puerto UDP
char incomingPacket[255];

// Función para conectar a Wi-Fi
void conectarWiFi() {
  WiFi.begin(ssid, password);
  Serial.println("Conectando a WiFi...");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Intentando conectar...");
    M5.Lcd.println("Intentando conectar...");
  }
  Serial.println("¡Conectado a WiFi!");
  M5.Lcd.println("¡Conectado a WiFi!");
  Serial.println("Dirección IP: " + WiFi.localIP().toString());
  M5.Lcd.println("Dirección IP: " + WiFi.localIP().toString());
}

bool comprobarHora() {
  // Obtener la hora y los minutos actuales desde el timeClient
  int horaActual = timeClient.getHours();
  int minutoActual = timeClient.getMinutes();

  // Formatear la hora actual como un número entero en formato HHMM
  int horaActualInt = horaActual * 100 + minutoActual;

  // Separar las horas de la lista usando la coma como delimitador
  int pos = 0;
  String lista = listaHoras + ",";
  while ((pos = lista.indexOf(',')) != -1) {
    String horaLista = lista.substring(0, pos);  // Extraer la hora de la lista
    lista = lista.substring(pos + 1);            // Eliminar la hora ya comparada

    // Convertir la hora de la lista a entero para comparación
    int horaListaInt = horaLista.toInt();

    // Comparar si la hora actual coincide con alguna de las horas en la lista
    if (horaListaInt == horaActualInt) {
      return true;  // Hora encontrada en la lista
    }
  }

  return false;  // No se encontró la hora
}

// Función para enviar datos por UART
void enviarUART() {
  if (dataChanged) {
    String mensaje = String("MOV:") + proximidadDetectada + "|UID:" + ultimoRFID + "|BOLLA:" + alertaDeBoya + "|DIST:" + alertaDeComida + "|PERC:" + porcentajeComida;

    Serial1.println(mensaje);
    Serial.println("Mensaje enviado por UART: " + mensaje);
    M5.Lcd.println("Mensaje enviado por UART: " + mensaje);
    dataChanged = false;
  }
}

// Función para procesar comandos recibidos por UART
void procesarComando(String comando) {
  if (comando.startsWith("a")) {
    racion = comando.substring(1).toInt();
    Serial.println("Nueva ración: " + String(racion) + " gramos");
    M5.Lcd.println("Nueva ración: " + String(racion) + " gramos");
  } else if (comando.startsWith("b")) {
    listaHoras = comando.substring(1);
    Serial.println("Nueva lista de horas: " + listaHoras);
    M5.Lcd.println("Nueva lista de horas: " + listaHoras);
  } else {
    Serial.println("Comando no reconocido.");
    M5.Lcd.println("Comando no reconocido.");
  }
}

// Manejo de comandos desde Serial1
void recibirUART() {
  if (Serial1.available() > 0) {
    String comando = Serial1.readStringUntil('\n');
    Serial.println(comando);
    procesarComando(comando);
  }
}

// Manejo de comandos desde Serial
void manejarComandosSerial() {
  if (Serial.available() > 0) {
    String input = Serial.readStringUntil('\n');
    input.trim();

    if (input == "send") {
      dataChanged = true;
      Serial.println("Tarea SendUART activada.");
    } else if (input == "see") {
      Serial.println("Estado actual:");
      Serial.println(" - Proximidad: " + String(proximidadDetectada));
      Serial.println(" - Último RFID: " + ultimoRFID);
      Serial.println(" - Alerta de boya: " + String(alertaDeBoya));
      Serial.println(" - Alerta de comida: " + String(alertaDeComida));
      Serial.println(" - Porcentaje de comida: " + String(porcentajeComida));
      Serial.println(" - Ración: " + String(racion) + " gramos");
      Serial.println(" - ListaHoras: " + listaHoras);
    }
  }
}

// Procesar el mensaje recibido por UDP
void procesarMensaje(String msg) {
  if (msg.startsWith("MOVIMIENTO")) {
    bool nuevoProximidad = (msg.indexOf("1") >= 0);
    if (proximidadDetectada != nuevoProximidad) {
      proximidadDetectada = nuevoProximidad;
      Serial.println(msg);
      M5.Lcd.println(msg);
      dataChanged = true;
    }
  } else if (msg.startsWith("UID")) {
    String nuevoUID = msg.substring(msg.indexOf(":") + 1);
    if (ultimoRFID != nuevoUID) {
      ultimoRFID = nuevoUID;
      Serial.println(msg);
      M5.Lcd.println(msg);
      dataChanged = true;
    }
  } else if (msg.startsWith("BOLLA")) {
    bool nuevaBoya = (msg.substring(msg.indexOf(":") + 1, msg.indexOf(" ")).toInt() == 1);
    bool nuevaDistancia = (msg.substring(msg.lastIndexOf(":") + 1).toInt() == 1);
    if (alertaDeBoya != nuevaBoya || alertaDeComida != nuevaDistancia) {
      alertaDeBoya = nuevaBoya;
      alertaDeComida = nuevaDistancia;
      Serial.println(msg);
      M5.Lcd.println(msg);
      dataChanged = true;
    }
  } else {
    Serial.println("Comando no reconocido.");
  }
}

// Recibir mensajes UDP
void recibirMensajesUDP() {
  int packetSize = udpServer.parsePacket();
  if (packetSize) {
    int len = udpServer.read(incomingPacket, 255);
    if (len > 0) {
      incomingPacket[len] = '\0';
    }
    String mensaje = String(incomingPacket);
    procesarMensaje(mensaje);
  }
}

// Función para autocalibrar la balanza
void autocalibrar() {
  Serial.println("[INFO] Iniciando autocalibración...");
  M5.Lcd.println("iniciando calibracion");

  // Esperar 3 segundos antes de comenzar
  Serial.println("[INFO] Esperando 3 segundos...");
  M5.Lcd.println("3segundos");
  delay(3000);

  // Asegurarse de que la balanza esté vacía y tararla
  Serial.println("[INFO] Asegúrese de que la balanza esté vacía.");
  M5.Lcd.println("balanza vacia");
  delay(5000);  // Esperar 5 segundos para vaciar la balanza
  balanza.tare();
  Serial.println("[INFO] Balanza tarada correctamente.");
  M5.Lcd.println("coloque 500 g");

  // Solicitar colocar el peso conocido
  Serial.println("[INFO] Coloque un peso conocido de 500 g en la balanza.");
  delay(5000);  // Esperar 5 segundos para colocar el peso

  // Calcular la escala
  long lecturaBruta = balanza.get_value(10);   // Obtener lectura promedio
  float escala = lecturaBruta / pesoConocido;  // Calcular escala
  balanza.set_scale(escala);                   // Configurar la escala

  // Finalizar calibración
  Serial.println("[INFO] Autocalibración completada.");
  Serial.print("[DEBUG] Escala calculada: ");
  Serial.println(escala, 6);  // Mostrar escala calculada

  M5.Lcd.println("listo " + String(escala));
}

// Función para mostrar el peso
int mostrarPeso() {
  if (balanza.is_ready()) {
    int peso = balanza.get_units(10);  // Obtener el peso promedio
    return peso;
  } else {
    Serial.println("[ERROR] Sensor no conectado o no responde.");
    return -1;  // Valor especial para indicar error
  }
}

void marcarPorcentaje() {
  int pesoActual = mostrarPeso();  // Obtener el peso actual de la balanza

  Serial.println(String(pesoActual) + " // " + String(racion));  // Debugging
  M5.Lcd.println(String(pesoActual) + " // " + String(racion));
  if (pesoActual >= 0) {                                             // Verificar que no haya error en la medición
    porcentajeComida = ((float)pesoActual / (float)racion) * 100.0;  // Calcular el porcentaje

    // Limitar el porcentaje a un máximo de 100%
    if (porcentajeComida > 100.0) {
      porcentajeComida = 100.0;
    }

    Serial.print("Porcentaje de comida restante: ");
    Serial.print(porcentajeComida);
    Serial.println("%");
    M5.Lcd.println(String(porcentajeComida) + "%");

    porcentajePendiente = false;  // Indicar que ya no hay error
    percent = true;               // Marcar que el cálculo fue exitoso
    dataChanged = true;
  } else {
    Serial.println("[ERROR] No se pudo obtener el peso del plato.");
    porcentajePendiente = true;  // Indicar que hay un error y se debe reintentar
  }
}


void mostrarHoraYMinuto() {
  // Actualizar el cliente NTP
  timeClient.update();

  // Obtener la hora y los minutos actuales
  int horaActual = timeClient.getHours();
  int minutoActual = timeClient.getMinutes();

  // Solo mostrar si el minuto ha cambiado
  if (minutoActual != ultimoMinuto) {
    ultimoMinuto = minutoActual;  // Actualizar el último minuto mostrado

    // Formatear y mostrar hora:minuto
    Serial.print("Hora actual: ");
    Serial.print(horaActual);
    Serial.print(":");
    if (minutoActual < 10) {
      Serial.print("0");  // Añadir un 0 si el minuto es menor a 10
    }
    Serial.println(minutoActual);
    M5.Lcd.println(String(horaActual) + ":" + String(minutoActual));
  }
}

void setup() {
  M5.begin();                                 // Inicializa el M5Stack
  M5.Lcd.clear();                             // Limpia la pantalla
  M5.Lcd.setTextSize(2);                      // Establece el tamaño del texto
  M5.Lcd.setTextColor(TFT_WHITE, TFT_BLACK);  // Texto blanco con fondo negro

  Serial.begin(115200);
  Serial1.begin(115200, SERIAL_8N1, 16, 17);

  balanza.begin(DOUT, CLK);
  autocalibrar();

  pinMode(clkPin, OUTPUT);
  digitalWrite(clkPin, LOW);

  conectarWiFi();
  timeClient.begin();
  udpServer.begin(localUdpPort);
  M5.Lcd.println("Servidor UDP iniciado.");
  Serial.printf("Servidor UDP iniciado en el puerto %d\n", localUdpPort);
}


void loop() {
  manejarComandosSerial();
  recibirUART();
  recibirMensajesUDP();

  mostrarHoraYMinuto();

  // Si se detecta que es hora de alimentar, intentar calcular el porcentaje
  if (comprobarHora()) {
    if (!percent || porcentajePendiente) {  // Ejecutar si no se ha calculado o hay error pendiente
      marcarPorcentaje();
    }

    int pesoActual = mostrarPeso();  // Obtener el peso actual
    if (pesoActual < racion) {       // Comparar peso con la ración
      if (!mensajeMostrado) {        // Solo imprimir si el mensaje no se ha mostrado
        Serial.println("¡Hora de comer! Activando motor...");
        M5.Lcd.println("¡Hora de comer!");
        mensajeMostrado = true;  // Marcar como mostrado
      }
      motorEncendido = true;  // Activar el motor si el peso es menor que la ración
    } else {
      if (!mensajeMostrado) {  // Solo imprimir si el mensaje no se ha mostrado
        Serial.println("Suficiente comida, no se activa el motor.");
        M5.Lcd.println("Suficiente comida.");
        mensajeMostrado = true;  // Marcar como mostrado
      }
      motorEncendido = false;
    }
  } else {
    percent = false;          // Reiniciar el control cuando comprobarHora() sea false
    mensajeMostrado = false;  // Resetear el estado de impresión al salir de la condición
  }

  if (motorEncendido) {
    unsigned long currentTime = micros();
    if (currentTime - lastStepTime >= stepInterval) {
      for (int i = 0; i < 75; i++) {  // Repite el pulso
        digitalWrite(clkPin, HIGH);
        delayMicroseconds(3);  // Ajusta según las capacidades del hardware
        digitalWrite(clkPin, LOW);
        delayMicroseconds(3);
      }
      lastStepTime = currentTime;
    }
  }

  motorEncendido = false;
  enviarUART();
  delay(100);
}
