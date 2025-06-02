package com.example.iotconsumer;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.TimeoutException;

@Component
public class RabbitConsumer {

  private static final String QUEUE_NAME = "iot.sensor01";
  private static final String RABBIT_HOST = "localhost";
  private static final int RABBIT_PORT = 31572;

  // Neo4j connection infos are in .evn

  // NEO4J_URI
  // NEO4J_USER
  // NEO4J_PASSWORD

  // RabbitMQ connection infos are in .evn
  // ENCODED_USERNAME
  // ENCODED_PASSWORD

  @Value("${encoded.username}")
  private String encodedUsername;

  @Value("${encoded.password}")
  private String encodedPassword;

  @Value("${neo4j.uri}")
  private String neo4jUri;

  @Value("${neo4j.user}")
  private String neo4jUser;

  @Value("${neo4j.password}")
  private String neo4jPassword;

  public void startConsuming() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(RABBIT_HOST);
    factory.setPort(RABBIT_PORT);

    String username = new String(Base64.getDecoder().decode(encodedUsername));
    String password = new String(Base64.getDecoder().decode(encodedPassword));

    factory.setUsername(username);
    factory.setPassword(password);

    Neo4jWriter writer = new Neo4jWriter(neo4jUri, neo4jUser, neo4jPassword);

    try {
      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();

      channel.queueDeclare(QUEUE_NAME, true, false, false, null);

      System.out.println("Waiting for message from RabbitMQ..");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("Received: [" + timestamp + "] " + message);

        // extract temperature from string
        if (message.contains("Temperature:")) {
          try {
            double temp = Double.parseDouble(message.split("Temperature:")[1].trim());
            writer.writeTemperature(timestamp, temp);
          } catch (NumberFormatException e) {
            System.err.println("Invalid temperature format: " + message);
          }
        }
      };

      channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
      });

    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }
}
