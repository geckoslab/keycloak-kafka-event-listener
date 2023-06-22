package com.geckoslab.keycloak.providers.events;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;

public class KafkaEventsListenerProvider implements EventListenerProvider {
  private static final Logger logger = Logger.getLogger(KafkaEventsListenerProvider.class.getName());

  private final KafkaProducer<String, String> producer;

  public KafkaEventsListenerProvider() {
    String KafkaBrokers = System.getenv("KAFKA_BROKERS") != null ? System.getenv("KAFKA_BROKERS") : "localhost:9092";

    Properties kafkaConfig = new Properties();
    kafkaConfig.put("bootstrap.servers", KafkaBrokers);
    kafkaConfig.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    kafkaConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    this.producer = new KafkaProducer<>(kafkaConfig);
  }

  @Override
  public void onEvent(Event event) {
    EventType eventType = event.getType();
    String realmId = event.getRealmId();
    String clientId = event.getClientId();
    String userId = event.getUserId();
    String sessionId = event.getSessionId();
    long time = event.getTime();
    Map<String, String> details = event.getDetails();

    String message = String.format(
      "{\"realmId\": \"%s\", \"clientId\": \"%s\", \"userId\": \"%s\", \"sessionId\": \"%s\", \"time\": \"%s\", \"details\": \"%s\"}",
      realmId,
      clientId,
      userId,
      sessionId,
      time,
      details
    );
    String topic = String.format("keycloak.event.%s", eventType);
    producer.send(new ProducerRecord<String, String>(topic, message));
  }

  @Override
  public void onEvent(AdminEvent event, boolean includeRepresentation) {
    OperationType operationType = event.getOperationType();
    ResourceType resourceType = event.getResourceType();
    String resourcePath = event.getResourcePath();
    String representation = event.getRepresentation();

    String message = String.format(
      "{\"operationType\": \"%s\", \"resourceType\": \"%s\", \"resourcePath\": \"%s\", \"representation\": \"%s\"}",
      operationType,
      resourceType,
      resourcePath,
      representation = representation.replace("\"", "\\\"")
    );
    String topic = String.format("keycloak.admin.event.%s.%s", operationType, resourceType);
    producer.send(new ProducerRecord<String, String>(topic, message));
  }

  @Override
  public void close() {
    producer.close();
  }
}
