package com.example.iotsimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class IotSimulatorApplication implements CommandLineRunner {

  @Autowired
  private MqttPublisher mqttPublisher;

  public static void main(String[] args) {
    SpringApplication.run(IotSimulatorApplication.class, args);

  }
  //

  // MqttPublisher publisher = new MqttPublisher();
  // publisher.startPublishing();

  // }

  @Override
  public void run(String... args) {
    mqttPublisher.startPublishing();
  }

}
