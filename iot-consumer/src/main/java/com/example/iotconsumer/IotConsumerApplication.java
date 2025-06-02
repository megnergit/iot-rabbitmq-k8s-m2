package com.example.iotconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class IotConsumerApplication implements CommandLineRunner {

  @Autowired
  private RabbitConsumer consumer;

  public static void main(String[] args) {
    SpringApplication.run(IotConsumerApplication.class, args);
  }

  @Override
  public void run(String... args) {
    consumer.startConsuming();
  }

}
