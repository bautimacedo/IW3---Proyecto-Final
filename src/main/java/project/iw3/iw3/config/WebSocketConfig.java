package project.iw3.iw3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker //Nos permite levantar un broker
//Un broker es el servidor que maneja las conexiones websocket.
//Topico: Sirve para etiquetar los datos que se envian a traves de la conexion websocket
//Si el front se suscribe al topico de temperaturas, el broker le va a enviar los datos de las temperaturas, en caso de que
//se esten enviando datos de otros topicos, el broker no se lo va a enviar.

public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.setApplicationDestinationPrefixes("/app"); //Camino inverso: Si el front le quiere enviar mensaje al back, la
        //la ruta tiene que empezar con /app.
       
        registry.enableSimpleBroker("/topic"); //Cualquier mensaje que empiece con /topic tiene que ser manejado por el broker.
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Un solo endpoint de conexión: el cliente se conecta aquí y se suscribe a /topic/temperaturas, /topic/densidad, /topic/caudal
        registry.addEndpoint("/temperaturas").setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/temperatures").setAllowedOriginPatterns("*").withSockJS();
    }

}
