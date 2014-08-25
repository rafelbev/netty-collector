package workshop.config;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import workshop.jms.Receiver;

import javax.jms.ConnectionFactory;

@Configuration
@Import(CoreConfig.class)
@ComponentScan({"workshop.util"})
@EnableAutoConfiguration
@PropertySource("classpath:receiver.properties")
public class DefaultConfig {

    @Value("${locationQueue:location-destination}")
    private String locationQueue;

    @Value("${brokerURL:tcp://localhost:61616}")
    private String brokerURL;

    @Bean
    Receiver receiver() {
        return new Receiver();
    }

    @Bean
    MessageListenerAdapter adapter(Receiver receiver) {
        MessageListenerAdapter messageListener
                = new MessageListenerAdapter(receiver);
        messageListener.setDefaultListenerMethod("receiveTrackingData");
        return messageListener;
    }

    @Bean
    ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(brokerURL);
    }

    @Bean
    SimpleMessageListenerContainer container(MessageListenerAdapter messageListener,
                                             ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setMessageListener(messageListener);
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName(locationQueue);
        return container;
    }

}
