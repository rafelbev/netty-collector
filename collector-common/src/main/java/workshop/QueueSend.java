package workshop;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.NamingException;
import java.io.Serializable;

public class QueueSend {

    public final static String QUEUE = "location-destination";

    private QueueConnectionFactory qconFactory;
    private QueueConnection qcon;
    private QueueSession qsession;
    private QueueSender qsender;
    private Queue queue;
    private ObjectMessage msg;

    /**
     * Creates all the necessary objects for sending
     * messages to a JMS queue.
     *
     * @param connectionString connection string used to connect to ActiveMQ
     * @param queueName        name of queue
     * @throws NamingException if operation cannot be performed
     * @throws JMSException    if JMS fails to initialize due to internal error
     */
    public QueueSend(String connectionString, String queueName)
            throws NamingException, JMSException {
        qconFactory = new ActiveMQConnectionFactory(connectionString);
        qcon = qconFactory.createQueueConnection();
        qsession = qcon.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = qsession.createQueue(queueName);
        qsender = qsession.createSender(queue);
        msg = qsession.createObjectMessage();
        qcon.start();
    }

    /**
     * Sends a message to a JMS queue.
     *
     * @param message message to be sent
     * @throws JMSException if JMS fails to send message due to internal error
     */
    public void send(Serializable message) throws JMSException {
        msg.setObject(message);
        qsender.send(msg);
    }

    /**
     * Closes JMS objects.
     *
     * @throws JMSException if JMS fails to close objects due to internal error
     */
    public void close() throws JMSException {
        qsender.close();
        qsession.close();
        qcon.close();
    }

}
