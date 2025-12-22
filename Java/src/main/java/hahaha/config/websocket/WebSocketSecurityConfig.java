package hahaha.config.websocket;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
@Configuration
public class WebSocketSecurityConfig
        extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                // CONNECT
                .nullDestMatcher().permitAll()

                // 🔒 STAFF ONLY
                .simpSubscribeDestMatchers("/topic/staff/**")
                .hasRole("STAFF")

                // 🔒 Conversation (user + staff assigned)
                .simpSubscribeDestMatchers("/topic/conversation/**")
                .authenticated()

                // SEND
                .simpDestMatchers("/app/**")
                .authenticated()

                // DISCONNECT
                .simpTypeMatchers(org.springframework.messaging.simp.SimpMessageType.DISCONNECT)
                .permitAll()

                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
