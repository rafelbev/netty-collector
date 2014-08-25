package workshop;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workshop.dto.HexDataTO;

import javax.jms.JMSException;
import javax.xml.bind.DatatypeConverter;

/**
 * Class to handle incoming UDP payloads and returns OK
 */
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger logger = LoggerFactory.getLogger(UdpServerHandler.class.getName());

    /**
     * Target queue to be used to collect all JSON data
     */
    private QueueSend queueSend;

    public UdpServerHandler(QueueSend queueSend) {
        this.queueSend = queueSend;
    }

    /**
     * Process incoming UDP packets and submit them to the queue
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        logger.info(packet.toString());
        ByteBuf content = packet.content();
        byte[] contentAsArray = new byte[content.readableBytes()];
        content.getBytes(0, contentAsArray);
        String hexBinary = DatatypeConverter.printHexBinary(contentAsArray);
        queueSend.send(new HexDataTO(hexBinary, DateTime.now().toDate()));
        ctx.write(new DatagramPacket(Unpooled.copiedBuffer("OK", CharsetUtil.US_ASCII), packet.sender()));
    }

    /**
     * Cleanup
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        try {
            queueSend.close();
        } catch (JMSException e) {
            logger.error("{}", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("{}", cause);
        // We don't close the channel because we can keep serving requests.
    }
}