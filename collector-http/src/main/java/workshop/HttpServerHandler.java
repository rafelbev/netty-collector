package workshop;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workshop.dto.ResultTO;
import workshop.dto.TrackingDataTO;
import workshop.enums.ResultCode;
import workshop.enums.TrackingType;

import javax.jms.JMSException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;

/**
 * Class to handle incoming HTTP JSON payloads and returns JSON HTTP status codes
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class.getName());

    /**
     * Collate the contents of requests across multiple HTTP packets
     */
    private final StringBuilder requestContent = new StringBuilder();

    /**
     * Collate the response generated from multiple HTTP packets
     */
    private final StringBuilder responseContent = new StringBuilder();

    /**
     * One GSON object generated per thread
     */
    private final Gson gson = new Gson();

    /**
     * Current HttpRequest;
     */
    private HttpRequest request;

    /**
     * Target queue to be used to collect all JSON data
     */
    private QueueSend queueSend;

    /**
     * Type of action being handled by this HTTP request
     */
    private TrackingType trackingType;

    public HttpServerHandler(QueueSend queueSend) {
        this.queueSend = queueSend;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            trackingType = setTrackingType(request);
        }

        if (msg instanceof HttpContent) {
            // New chunk is received
            HttpContent chunk = (HttpContent) msg;
            requestContent.append(chunk.content().toString(Charset.defaultCharset()));
            if (chunk instanceof LastHttpContent) {
                // All chunks have been received
                ResultTO resultTO = submitPayload(requestContent.toString());
                responseContent.append(gson.toJson(resultTO));
                writeResponse(ctx.channel());
                reset();
            }
        }
    }

    /**
     * Accepts JSON object and submits it to the JMS queue for processing
     * @param requestAsJson JSON formatted string. See {@link workshop.dto.TrackingDataTO}
     * @return ResultTO to be passed on to the HTTP client with possible errorcodes
     */
    public ResultTO submitPayload(String requestAsJson) {
        ResultTO resultTO;
        if (trackingType == TrackingType.UNKNOWN) {
            resultTO = new ResultTO(ResultCode.UNKNOWN_ROUTE);
        } else {
            try {
                TrackingDataTO trackingDataTO = gson.fromJson(requestAsJson, TrackingDataTO.class);
                trackingDataTO.setType(trackingType);
                trackingDataTO.setTimestamp(DateTime.now().toDate());
                queueSend.send(trackingDataTO);
                resultTO = new ResultTO(ResultCode.SUCCESS);
            } catch (JsonSyntaxException e) {
                resultTO = new ResultTO(ResultCode.MALFORMED_INPUT);
            } catch (JMSException e) {
                resultTO = new ResultTO(ResultCode.INTERNAL_ERROR);
            }
        }
        return resultTO;
    }

    /**
     * Returns the route URI for the current request
     * @param request HttpRequest which contains the request URI of the current session
     * @return Returns the route
     * @throws URISyntaxException
     */
    public TrackingType setTrackingType(HttpRequest request) throws URISyntaxException {
        URI uri = new URI(request.getUri());
        if (uri.getPath().startsWith("/trackingPoint")) {
            return TrackingType.TRACKING_POINT;
        } else if (uri.getPath().startsWith("/startTracking")) {
            return TrackingType.START_TRACKING;
        } else if (uri.getPath().startsWith("/stopTracking")) {
            return TrackingType.STOP_TRACKING;
        } else {
            return TrackingType.UNKNOWN;
        }
    }

    /**
     * Cleanup
     */
    private void reset() {
        request = null;
        requestContent.setLength(0);
        responseContent.setLength(0);
        try {
            queueSend.close();
        } catch (JMSException e) {
            logger.error("{}", e);
        }
    }

    /**
     * Flush the response contents to the output channel
     * @param channel Channel to be used
     */
    private void writeResponse(Channel channel) {
        // Convert the response content to a ChannelBuffer.
        ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
        responseContent.setLength(0);

        // Decide whether to close the connection or not.
        boolean close = HttpHeaders.Values.CLOSE.equalsIgnoreCase(request.headers().get(CONNECTION))
                || request.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
                && !HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(request.headers().get(CONNECTION));

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8");

        if (!close) {
            // There's no need to add 'Content-Length' header
            // if this is the last response.
            response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        }

        try {
            // Write the response.
            ChannelFuture future = channel.writeAndFlush(response);
            // Close the connection after the write operation is done if necessary.
            if (close) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn(responseContent.toString(), cause);
        ctx.channel().close();
    }
}