package workshop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * An HTTP server accepting different types of JSON payloads to be passed to a JMS Queue
 */
public final class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class.getName());

    /**
     * Set the 'ssl' environment variable to switch to using HTTPS
     */
    private final boolean SSL = System.getProperty("ssl") != null;

    /**
     * Set the 'port' environment variable to override the default port
     */
    private final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));

    /**
     * Set the 'connectionSettings' environment variable to override the default JMS queue connection settings
     */
    private final String connectionSettings = System.getProperty("connectionSettings", "tcp://localhost:61616") ;

    private SslContext sslContext;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;

    /**
     * Initialises the object, threads, and SSL context
     */
    public HttpServer() {
        sslContext = initialiseSSL();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
    }

    /**
     * Starts the execution of the server
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        serverBootstrap.childHandler(new HttpServerInitializer(connectionSettings, sslContext));

        channel = serverBootstrap.bind(PORT).sync().channel();
        logger.info("HTTP Server listening on port {}", PORT);
    }

    /**
     * Waits for the event when the channel is closed and exits gracefully
     */
    public void syncAndStop() {
        try {
            channel.closeFuture().sync();
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
        bossGroup.shutdownGracefully();
        logger.info("Server has been shutdown gracefully");
    }

    /**
     * Creates an SSL context
     * @return a self signed SSL context to be used for HTTPS connections
     */
    private SslContext initialiseSSL() {
        // Configure SSL.
        SslContext sslCtx = null;
        if (SSL) {
            try {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
            } catch (CertificateException e) {
            } catch (SSLException e) {
            }
        }
        return sslCtx;
    }

    /**
     * Launches a standalone HTTP collector directly
     * @param args No arguments are needed
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        HttpServer httpServer = new HttpServer();
        httpServer.start();
        httpServer.syncAndStop();
    }
}