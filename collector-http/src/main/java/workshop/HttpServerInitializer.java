package workshop;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

import javax.jms.JMSException;
import javax.naming.NamingException;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final String connectionSettings;
    private final SslContext sslCtx;

    public HttpServerInitializer(String connectionSettings, SslContext sslCtx) {
        this.connectionSettings = connectionSettings;
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws JMSException, NamingException {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpResponseEncoder());

        // Remove the following line if you don't want automatic content compression.
        pipeline.addLast(new HttpContentCompressor());

        pipeline.addLast(new HttpServerHandler(new QueueSend(connectionSettings, QueueSend.QUEUE)));
    }
}