package com.example.iotconsumer;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

public class Neo4jWriter {

  public final Driver driver;

  public Neo4jWriter(String uri, String user, String password) {
    driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
  }

  public void writeTemperature(String timestamp, double temperature) {
    try (Session session = driver.session()) {
      session.run(
          "MERGE (s:Sensor {name: 'sensor01'}) " +
              "CREATE (r:Reading {value: $temp, time: $time})" +
              "MERGE (s)-[:SENT]->(r) ",
          org.neo4j.driver.Values.parameters("temp", temperature,
              "time", timestamp));
    }
  }

  public void close() {
    driver.close();

  }

}
