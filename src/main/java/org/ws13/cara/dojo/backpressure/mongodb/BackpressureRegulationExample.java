package org.ws13.cara.dojo.backpressure.mongodb;

import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.ws13.cara.dojo.backpressure.mongodb.util.DojoUtil;
import org.ws13.cara.dojo.backpressure.mongodb.util.RequestPaceSubscriber;

import java.util.Random;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author ctranxuan
 */
public final class BackpressureRegulationExample {
    private static final Logger LOGGER = getLogger(SubscriberWithRequestExample.class);
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        MongoCollection<Document> collection = DojoUtil.createCollection();

        Flowable<Document> documentFlowable = Flowable.fromPublisher(collection.find());

        documentFlowable
                .doOnNext(d -> LOGGER.info("publisher sends document {}", d.get("name")))
                .subscribeWith(new Subscriber<Document>() {
                    private Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription aSubscription) {
                        subscription = aSubscription;
                        // request 1 document at subscription
                        aSubscription.request(1);
                    }

                    @Override
                    public void onNext(Document aDocument) {
                        processJob(aDocument);
                    }

                    @Override
                    public void onError(Throwable aThrowable) {
                        LOGGER.error("error occured!", aThrowable);
                    }

                    @Override
                    public void onComplete() {
                        LOGGER.info("That's all folks!");
                    }

                    private void processJob(Document aDocument) {
                        try {
                            long sleep = RANDOM.nextInt(10) * 1000;
                            LOGGER.info("{} ⇶ processing the document: {} (processing took during {})", aDocument, sleep);
                            Thread.sleep(sleep);
                            /*
                             * job is done: we request from the upstream a new document to process
                             */
                            subscription.request(1);

                        } catch (InterruptedException aE) {
                            aE.printStackTrace();
                        }
                    }
                });

        Flowable.timer(1, MINUTES)
                .blockingSubscribe();
    }


}
