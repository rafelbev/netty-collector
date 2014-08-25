package workshop;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import workshop.config.DefaultConfig;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class Demo {

    public static void main(String[] args) {
        // Load spring context with the config for the engine-receiver
        ConfigurableApplicationContext context = SpringApplication.run(DefaultConfig.class, args);
        // Initialise the HTTP collector
        HttpServer httpServer = new HttpServer();
        // Initialise the UDP collector
        UdpServer udpServer = new UdpServer();
        try {
            // Start and bind the port
            httpServer.start();
            // Start and bind the port
            udpServer.start();

            // Wait till all connections close
            httpServer.syncAndStop();
            // Wait till all connections close
            udpServer.syncAndStop();
        } catch (InterruptedException | JMSException | NamingException e) {
            e.printStackTrace();
        } finally {
            // Close gracefully
            httpServer.stop();
            udpServer.stop();
        }
    }
}
