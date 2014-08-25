package workshop;

import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workshop.dto.ResultTO;
import workshop.dto.TrackingDataTO;
import workshop.enums.ResultCode;
import workshop.enums.TrackingType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class HttpServerIT {

    public static final int CONCURRENT_REQUESTS = 100;
    public static final int TIMEOUT = 10000;

    Logger log = LoggerFactory.getLogger(HttpServerIT.class);

    private Random random;

    private Gson gson;

    private HttpServer httpServer;
    private int counter;

    @Before
    public void init() throws InterruptedException {
        counter = 0;
        random = new Random(DateTime.now().getMillis());
        gson = new Gson();
        httpServer = new HttpServer();
        httpServer.start();
    }

    @After
    public void tearDown() {
        httpServer.stop();
    }

    @Test
    public void testHttpServer() throws IOException, ExecutionException, InterruptedException {
        final CountDownLatch lock = new CountDownLatch(CONCURRENT_REQUESTS);

        for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
            TrackingDataTO trackingDataTO = bootstrapTrackingData();
            String requestAsJson = gson.toJson(trackingDataTO);

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.preparePost("http://localhost:8080/" + trackingDataTO.getType().getLabel())
                    .setBody(requestAsJson)
                    .execute(new AsyncCompletionHandler<Response>() {

                        @Override
                        public Response onCompleted(Response response) throws Exception {
                            ResultTO resultTo = gson.fromJson(response.getResponseBody(), ResultTO.class);
                            if (resultTo.getCode().equals(ResultCode.SUCCESS)) {
                                lock.countDown();
                            }
                            return response;
                        }

                        @Override
                        public void onThrowable(Throwable t) {
                            log.error(t.toString());
                        }
                    });
        }

        lock.await(TIMEOUT, TimeUnit.MILLISECONDS);
        assertEquals(0, lock.getCount());
    }

    private TrackingDataTO bootstrapTrackingData() {
        TrackingDataTO trackingDataTO = new TrackingDataTO();
        trackingDataTO.setType(randomRoute());
        trackingDataTO.setUpdatetime(new Date(random.nextLong()).toString());
        trackingDataTO.setCarrier(random.nextInt());
        trackingDataTO.setLatitude(BigDecimal.valueOf(random.nextLong()));
        trackingDataTO.setLongitude(BigDecimal.valueOf(random.nextLong()));
        trackingDataTO.setSpeed(BigDecimal.valueOf(random.nextLong()));
        trackingDataTO.setHeading(random.nextInt());
        trackingDataTO.setSatellites(random.nextInt());
        trackingDataTO.setFixStatus(random.nextInt());
        trackingDataTO.setCarrier(random.nextInt());
        trackingDataTO.setRssi(random.nextInt());
        trackingDataTO.setCommState(random.nextInt());
        trackingDataTO.setHdop(BigDecimal.valueOf(random.nextLong()));
        trackingDataTO.setInputs(random.nextInt());
        trackingDataTO.setUnitStatus(random.nextInt());
        trackingDataTO.setEventIndex(getNextCounter());
        trackingDataTO.setEventCode(random.nextInt());
        trackingDataTO.setSpare(random.nextInt());
        List<Integer> accum = bootstrapAccumsData();
        trackingDataTO.setAccums(accum.size());
        trackingDataTO.setAccum(accum);
        return trackingDataTO;
    }

    private List<Integer> bootstrapAccumsData() {
        int totalAccums = random.nextInt(10);
        List<Integer> accums = new ArrayList<>(totalAccums);
        for (int i = 0; i < totalAccums; i++) {
            accums.add(random.nextInt(10));
        }
        return accums;
    }

    private TrackingType randomRoute() {
        int nextRoute = random.nextInt(3);
        switch (nextRoute) {
            case 0:
                return TrackingType.START_TRACKING;
            case 1:
                return TrackingType.TRACKING_POINT;
            case 2:
                return TrackingType.STOP_TRACKING;
            default:
                return TrackingType.UNKNOWN;
        }
    }

    public Integer getNextCounter() {
        return counter++;
    }
}
