#include "WiFi.h"
#include "AsyncUDP.h"
#include <SPI.h>
#include <MFRC522.h>

#define PIR_PIN 21  // Pin GPIO conectado a la señal del sensor PIR

#define RST_PIN 22  // Pin para el reset del RC522
#define SS_PIN 5    // Pin para el SS (SDA) del RC522

// Configuración de red WiFi
const char* ssid = "aaaa";       // Nombre de tu red WiFi
const char* password = "12345678";  // Contraseña de tu red WiFi

AsyncUDP udp;
MFRC522 mfrc522(SS_PIN, RST_PIN); // Crear el objeto para el RC522

bool movementDetected = false;  // Estado del movimiento
bool rfidActive = false;        // Estado del lector RFID

// Función para imprimir el UID de la tarjeta
void printArray(byte *buffer, byte bufferSize) {
    for (byte i = 0; i < bufferSize; i++) {
        Serial.print(buffer[i] < 0x10 ? " 0" : " ");
        Serial.print(buffer[i], HEX);
    }
    Serial.println();
}

void setup() {
    Serial.begin(115200);  // Iniciar el monitor serial

    // Configuración de la conexión WiFi
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, password);

    Serial.println("Conectando a WiFi...");
    unsigned long startAttemptTime = millis();

    while (WiFi.status() != WL_CONNECTED && millis() - startAttemptTime < 10000) {
        Serial.print(".");
        delay(500);
    }

    if (WiFi.status() == WL_CONNECTED) {
        Serial.println("\nConexión exitosa!");
        Serial.print("IP asignada: ");
        Serial.println(WiFi.localIP());
    } else {
        Serial.println("\nError al conectar a WiFi.");
        while (true) {
            delay(1000);  // Detener si no hay WiFi
        }
    }

    // Configurar el sensor PIR
    pinMode(PIR_PIN, INPUT);  // Configurar el pin PIR como entrada

    // Iniciar la comunicación SPI para el RFID
    SPI.begin();
    mfrc522.PCD_Init();

    Serial.println("Esperando movimiento y lectura de tarjetas...");

    // Configurar el listener UDP
    if (udp.listen(1234)) {
        Serial.print("UDP escuchando en la IP: ");
        Serial.println(WiFi.localIP());
    }
}

void loop() {
    // Leer el estado del sensor PIR
    int pirState = digitalRead(PIR_PIN);

    if (pirState == HIGH) {
        if (!movementDetected) {
            movementDetected = true;
            rfidActive = true;

            // Enviar mensaje de movimiento detectado
            udp.broadcastTo("MOVIMIENTO:1", 1234);
            Serial.println("MOVIMIENTO:1 enviado por UDP");
        }

        if (rfidActive) {
            // Leer tarjetas RFID mientras haya movimiento
            if (mfrc522.PICC_IsNewCardPresent()) {
                Serial.println("Tarjeta detectada. Procedemos a leer el UID:");

                if (mfrc522.PICC_ReadCardSerial()) {
                    // Leer UID de la tarjeta
                    Serial.print(F("Card UID: "));
                    printArray(mfrc522.uid.uidByte, mfrc522.uid.size);

                    // Formatear el UID para enviar por UDP
                    char uidText[50];
                    sprintf(uidText, "UID:");
                    for (byte i = 0; i < mfrc522.uid.size; i++) {
                        sprintf(uidText + strlen(uidText), "%02X", mfrc522.uid.uidByte[i]);
                        if (i < mfrc522.uid.size - 1) {
                            strcat(uidText, ":");
                        }
                    }

                    // Enviar el UID por UDP
                    udp.broadcastTo(uidText, 1234);
                    Serial.print("UID enviado por UDP: ");
                    Serial.println(uidText);

                    // Detener comunicación con la tarjeta actual
                    mfrc522.PICC_HaltA();

                }
            }
        }
    } else {
        if (movementDetected) {
            movementDetected = false;
            rfidActive = false;

            // Enviar mensaje de no movimiento detectado
            udp.broadcastTo("MOVIMIENTO:0", 1234);
            Serial.println("MOVIMIENTO:0 enviado por UDP");
        }
        delay(500);  // Asegurar tiempo muerto mientras no hay movimiento
    }
}
