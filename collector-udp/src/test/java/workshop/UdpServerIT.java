package workshop;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.naming.NamingException;
import java.net.InetSocketAddress;

import static org.junit.Assert.fail;

public class UdpServerIT {

    private static final int PORT = 7686;
    public static final int TIMEOUT = 5000;

    private static Logger logger = LoggerFactory.getLogger(UdpServerIT.class);

    private UdpServer udpServer;

    @Before
    public void init() throws InterruptedException, JMSException, NamingException {
        udpServer = new UdpServer();
        udpServer.start();
    }

    @After
    public void tearDown() {
        udpServer.stop();
    }

    @Test
    public void testUdpServer() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new UdpClientHandler());

            Channel ch = b.bind(0).sync().channel();

            for (int i = 0; i < 10; i++) {
                ch.writeAndFlush(new DatagramPacket(
                        Unpooled.copiedBuffer("HEXDATA" + i, CharsetUtil.UTF_8),
                        new InetSocketAddress("127.0.0.1", PORT)));
            }

            if (!ch.closeFuture().await(TIMEOUT)) {
                logger.error("Request timed out.");
                fail("Request timed out");
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
