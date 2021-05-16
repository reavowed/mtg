package mtg.web

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.{EnableWebSocketMessageBroker, StompEndpointRegistry, WebSocketMessageBrokerConfigurer}

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig extends WebSocketMessageBrokerConfigurer {
  override def configureMessageBroker(config: MessageBrokerRegistry): Unit = {
    config.enableSimpleBroker("/topic", "/user")
  }

  override def registerStompEndpoints(registry: StompEndpointRegistry): Unit = {
    registry
      .addEndpoint("/state")
      .withSockJS
  }
}
