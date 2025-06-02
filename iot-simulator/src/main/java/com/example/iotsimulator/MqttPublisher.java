package com.example.iotsimulator;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.Base64;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MqttPublisher {

  @Value("${encoded.username}")
  private String encodedUsername;

  @Value("${encoded.password}")
  private String encodedPassword;

  public void startPublishing() {
    String broker = "tcp://localhost:30883"; // RabbitM! MQTT port
    String clientId = "iot-simulator";
    String topic = "iot.sensor01";

    // connection infos are stored in .env (which is not committed)

    // System.out.println("USERNAME raw = [" + encodedUsername + "]");
    // System.out.println("PASSWORD raw = [" + encodedPassword + "]");

    String username = new String(Base64.getDecoder().decode(encodedUsername));
    String password = new String(Base64.getDecoder().decode(encodedPassword));

    MqttConnectOptions connOpts = new MqttConnectOptions();
    connOpts.setUserName(username);
    connOpts.setPassword(password.toCharArray());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    try {
      MqttClient client = new MqttClient(broker, clientId);
      client.connect(connOpts);

      for (int i = 0; i < 10; i++) {
        String timestamp = LocalDateTime.now().format(formatter);

        String content = "[" + timestamp + "] Temperature: " + (20 + Math.random() * 10);
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(1);

        client.publish(topic, message);
        System.out.println("Published: " + content);
        Thread.sleep(1000);
      }

      Thread.sleep(30000); // to see connection on RabbitMQ web UI
      client.disconnect();

      System.out.println("All messages sent. Will exit.");
      System.exit(0);

    } catch (MqttException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}