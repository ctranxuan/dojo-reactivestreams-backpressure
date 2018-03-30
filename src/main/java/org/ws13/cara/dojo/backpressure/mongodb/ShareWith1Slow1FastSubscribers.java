package org.ws13.cara.dojo.backpressure.mongodb;

import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;
import org.apache.logging.log4j.Logger;
import org.ws13.cara.dojo.backpressure.mongodb.util.PaceSubscriber;
import org.ws13.cara.dojo.backpressure.mongodb.util.RequestPaceSubscriber;

import java.util.concurrent.atomic.AtomicLong;

import static io.reactivex.schedulers.Schedulers.computation;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author ctranxuan (streamdata.io)
 */
public final class ShareWith1Slow1FastSubscribers {
    private static final class PaceSubscriber<T> extends DisposableSubscriber<T> {
        private static final Logger     LOGGER = getLogger(org.ws13.cara.dojo.backpressure.mongodb.util.PaceSubscriber.class);

        private final        String     name;
        private final        long       sleep;
        private final        long       request;

        PaceSubscriber(String aName, long aSleepInMillis, long aRequest) {
            name = requireNonNull(aName);
            sleep = aSleepInMillis;
            request = aRequest;
        }

        @Override
        protected void onStart() {
            LOGGER.info("{} ⇶ starting to subscribe [request={}, sleep={}]", name, request, sleep);

            request(request);
        }

        @Override
        public void onNext(T aNext) {
            try {
                Thread.sleep(sleep);
                request(request);

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

    public static void main(String[] args) {
        Flowable<Long> sharedFlowable =
                Flowable.generate(() -> 0L, (state, emitter) -> {
                    emitter.onNext(state);
                    return state + 1;
                })
                        .share()
                        .cast(Long.class);

        sharedFlowable
                .observeOn(computation())
                .subscribeWith(new PaceSubscriber<>("ford", 1, Long.MAX_VALUE));

        sharedFlowable
                .observeOn(computation())
                .subscribeWith(new PaceSubscriber<>("arthur", 1000, 1));

        Flowable.timer(10, MINUTES)
                .blockingSubscribe();
    }
}
