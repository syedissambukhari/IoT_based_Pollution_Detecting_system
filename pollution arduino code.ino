#include <Arduino.h>
#if defined(ESP32)
#include <WiFi.h>
#elif defined(ESP8266)
#include <ESP8266WiFi.h>
#endif
#include <Firebase_ESP_Client.h>
#include <DHT.h>

// Provide the token generation process info.
#include "addons/TokenHelper.h"
// Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

// Insert your network credentials
#define WIFI_SSID "Your wifi name"
#define WIFI_PASSWORD "Your wifi password"

// Insert Firebase project API Key
#define API_KEY "your firebase api key"

// Insert RTDB URL
#define DATABASE_URL "put your firebase realtiime databasee link"

// Define Firebase Data object
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
bool signupOK = false;

// Pin Definitions
#define RAIN_SENSOR_PIN A0
#define MQ2_SENSOR_PIN D0
#define FLAME_SENSOR_PIN D1
#define BUZZER_PIN D2
#define L298N_IN2_PIN D6
#define L298N_IN3_PIN D7

// Threshold Values
#define RAIN_THRESHOLD 1000 // Set your desired rain threshold value
#define MQ2_THRESHOLD 0  // Set your desired MQ2 gas threshold value
#define FLAME_THRESHOLD 0  // Set your desired flame threshold value
#define TEMPERATURE_THRESHOLD 40  // Set your desired temperature threshold value
// DHT Sensor
#define DHT_PIN D5
#define DHT_TYPE DHT11
DHT dht(DHT_PIN, DHT_TYPE);

void setup() {
  Serial.begin(115200);
  pinMode(RAIN_SENSOR_PIN, INPUT);
  pinMode(MQ2_SENSOR_PIN, INPUT);
  pinMode(FLAME_SENSOR_PIN, INPUT);
  pinMode(BUZZER_PIN, OUTPUT);
  pinMode(L298N_IN2_PIN, OUTPUT);
  pinMode(L298N_IN3_PIN, OUTPUT);

  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  /* Assign the api key (required) */
  config.api_key = API_KEY;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;

  /* Sign up */
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("ok");
    signupOK = true;
  } else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; // see addons/TokenHelper.h

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  dht.begin();
}

void loop() {
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 5000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();

    // Perform your sensor checks and alerts here
    // Read DHT sensor values
    float temperature = dht.readTemperature();
    float humidity = dht.readHumidity();

    if (isnan(temperature) || isnan(humidity)) {
      Serial.println("Failed to read from DHT sensor!");
      return;
    }

    // Display sensor values on the serial monitor
    Serial.print("Temperature: ");
    Serial.print(temperature);
    Serial.print(" °C\tHumidity: ");
    Serial.print(humidity);
    Serial.print(" %\t");

    // Check temperature threshold
    if (temperature > TEMPERATURE_THRESHOLD) {
      // Send an alert to Firebase or perform any required action
      Firebase.RTDB.setString(&fbdo, "Alert", "High temperature detected");
      Serial.println("High temperature detected");
      digitalWrite(BUZZER_PIN, HIGH);  // Activate the buzzer
    } else {
      // No alert condition
      Firebase.RTDB.setString(&fbdo, "Alert", "No alert");
      Serial.println("No alert");
      digitalWrite(BUZZER_PIN, LOW);   // Deactivate the buzzer
    }

    // Control L298N based on flame sensor and DHT sensor readings
    int flameValue = digitalRead(FLAME_SENSOR_PIN);
    if (flameValue == HIGH) {
      // Flame detected, turn on IN2
      digitalWrite(L298N_IN2_PIN, HIGH);
    } else {
      // No flame detected, turn off IN2
      digitalWrite(L298N_IN2_PIN, LOW);
    }

    if (humidity > TEMPERATURE_THRESHOLD) {
      // High humidity detected, turn on IN3
      digitalWrite(L298N_IN3_PIN, HIGH);
    } else {
      // Humidity within threshold, turn off IN3
      digitalWrite(L298N_IN3_PIN, LOW);
    }

    // Send temperature and humidity values to Firebase
    Firebase.RTDB.setFloat(&fbdo, "Temperature", temperature);
    Firebase.RTDB.setFloat(&fbdo, "Humidity", humidity);

    // Read rain sensor value
    int rainValue = analogRead(RAIN_SENSOR_PIN);

    // Read MQ2 gas sensor value
    int mq2Value = digitalRead(MQ2_SENSOR_PIN);

    // Display sensor values on the serial monitor
    Serial.print("Rain Sensor: ");
    Serial.print(rainValue);
    Serial.print("\tMQ2 Sensor: ");
    Serial.print(mq2Value);
    Serial.print("\tFlame Sensor: ");
    Serial.println(flameValue);

    if (rainValue <= RAIN_THRESHOLD) {
      // Send an alert to Firebase or perform any required action
      Firebase.RTDB.setString(&fbdo, "Alert", "Rain detected");
      Serial.println("Rain detected");
      digitalWrite(BUZZER_PIN, HIGH);  // Activate the buzzer
    } else {
      // No alert condition
      Firebase.RTDB.setString(&fbdo, "Alert", "No alert");
      Serial.println("No alert");
      digitalWrite(BUZZER_PIN, LOW);   // Deactivate the buzzer
    }

    if (mq2Value == 1) {
      // Send an alert to Firebase or perform any required action
      Firebase.RTDB.setString(&fbdo, "Alert", "Gas detected");
      Serial.println("Gas detected");
      digitalWrite(BUZZER_PIN, HIGH);  // Activate the buzzer
    } else {
      // No alert condition
      Firebase.RTDB.setString(&fbdo, "Alert", "No alert");
      Serial.println("No alert");
      digitalWrite(BUZZER_PIN, LOW);   // Deactivate the buzzer
    }

    // Send sensor values to Firebase
    Firebase.RTDB.setInt(&fbdo, "RainSensor", rainValue);
    Firebase.RTDB.setInt(&fbdo, "MQ2Sensor", mq2Value);
    Firebase.RTDB.setInt(&fbdo, "FlameSensor", flameValue);

    delay(1000); // Delay for 5 seconds before reading sensor values again
  }
}
