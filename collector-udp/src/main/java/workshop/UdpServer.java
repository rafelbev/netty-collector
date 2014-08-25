package workshop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.naming.NamingException;

/**
 * A UDP Server receiving the payload
 */
public final class UdpServer {

    private static final Logger logger = LoggerFactory.getLogger(UdpServer.class.getName());

    private final int PORT = Integer.parseInt(System.getProperty("port", "7686"));
    private final String connectionSettings = System.getProperty("connectionSettings", "tcp://localhost:61616") ;

    private EventLoopGroup eventLoopGroup;
    private Channel channel;

    /**
     * Initialises the event loop
     */
    public UdpServer() {
         eventLoopGroup = new NioEventLoopGroup();
    }

    /**
     * Starts the execution of the server
     * @throws InterruptedException
     */
    public void start() throws InterruptedException, JMSException, NamingException {
        QueueSend queueSend = new QueueSend(connectionSettings, QueueSend.QUEUE);
        UdpServerHandler udpServerHandler = new UdpServerHandler(queueSend);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(udpServerHandler);
        channel = bootstrap.bind(PORT).sync().channel();
        logger.info("UDP Server listening on port {}", PORT);
    }

    /**
     * Waits for the event when the channel is closed and exits gracefully
     */
    public void syncAndStop() {
        try {
            channel.closeFuture().await();
        } catch (InterruptedException e) {
            logger.info("Channel has been interrupted");
        } finally {
            stop();
        }
    }

    /**
     * Cleans up all threads and exits cleanly
     */
    public void stop() {
        eventLoopGroup.shutdownGracefully();
        logger.info("Server has been shutdown gracefully");
    }

    /**
     * Launches a standalone UDP collector directly
     * @param args No arguments are needed
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        UdpServer udpServer = new UdpServer();
        udpServer.syncAndStop();
    }

}