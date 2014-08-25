package workshop;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import workshop.config.DefaultConfig;
import workshop.dto.TrackingDataTO;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DefaultConfig.class)
public class ReceiveMessageTest {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveMessageTest.class.getName());

    @Value("${locationQueue:location-destination}")
    private String locationQueue;

    @Autowired
    private ApplicationContext context;

    @Test
    public void test() {
        // Send a message
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TrackingDataTO trackingDataTO = new TrackingDataTO();
                trackingDataTO.setLatitude(BigDecimal.ONE);
                trackingDataTO.setLongitude(BigDecimal.ONE);
                return session.createObjectMessage(trackingDataTO);
            }
        };
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        logger.debug("Sending a new message {}", messageCreator);
        jmsTemplate.send(locationQueue, messageCreator);
    }
}
