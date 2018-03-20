package org.ws13.cara.dojo.backpressure.mongodb.util;

import io.reactivex.subscribers.DisposableSubscriber;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.requireNonNull;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author ctranxuan
 */
public class RequestPaceSubscriber<T> extends DisposableSubscriber<T> {
    private static Logger LOGGER = getLogger(PaceSubscriber.class);
    private final String name;
    private final long sleep;
    private final long request;
    private final AtomicLong counter;

    public RequestPaceSubscriber(String aName, long aSleepInMillis, long aRequest) {
        name = requireNonNull(aName);
        sleep = aSleepInMillis;
        request = aRequest;
        counter = new AtomicLong();
    }

    @Override
    protected void onStart() {
        LOGGER.info("{} ⇶ starting to subscribe [request={}, sleep={}]", name, request, sleep);

        request(request);
        counter.set(request);
    }

    @Override
    public void onNext(T aNext) {

        if (counter.decrementAndGet() == 0) {
            try {
                    Thread.sleep(sleep);
                    if (request > 1) {
                        System.out.println();

                    }

                    request(request);
                    counter.set(request);

            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }

        LOGGER.info("{} ⇶ received onNext(): {}", name, aNext);

    }

    @Override
    public void onError(Throwable aThrowable) {
        LOGGER.error(aThrowable);
    }

    @Override
    public void onComplete() {
        LOGGER.info("{} ⇶ onComplete()", name);
    }


}
