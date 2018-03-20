package org.ws13.cara.dojo.backpressure.mongodb.util;

import io.reactivex.subscribers.DisposableSubscriber;
import org.apache.logging.log4j.Logger;

import static java.util.Objects.requireNonNull;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author ctranxuan
 */
public final class PaceSubscriber<T> extends DisposableSubscriber<T> {
    private static Logger LOGGER = getLogger(PaceSubscriber.class);
    private final String name;
    private final long sleep;


    public PaceSubscriber(String aName, long aSleepInMillis) {
        name = requireNonNull(aName);
        sleep = aSleepInMillis;
    }

    @Override
    protected void onStart() {
        LOGGER.info("{} ⇶ starting to subscribe [request={}, sleep={}]", name, sleep);
        super.onStart();
    }

    @Override
    public void onNext(T aNext) {
        try {
            Thread.sleep(sleep);

        } catch (InterruptedException e) {
            e.printStackTrace();

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
