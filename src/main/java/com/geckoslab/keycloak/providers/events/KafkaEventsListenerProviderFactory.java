package com.geckoslab.keycloak.providers.events;

import org.keycloak.Config.Scope;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class KafkaEventsListenerProviderFactory implements EventListenerProviderFactory {
  @Override
  public String getId() {
    return "geckoslab-kafka-events-listener";
  }

  @Override
  public void init(Scope config) {
    // Do nothing
  }

  @Override
  public EventListenerProvider create(KeycloakSession session) {
    return new KafkaEventsListenerProvider();
  }

  @Override
  public void postInit(KeycloakSessionFactory factory) {
    // Do nothing
  }

  @Override
  public void close() {
    // Do nothing
  }
}
