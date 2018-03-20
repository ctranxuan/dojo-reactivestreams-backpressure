package org.ws13.cara.dojo.backpressure.mongodb;

import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.Flowable;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.ws13.cara.dojo.backpressure.mongodb.util.DojoUtil;
import org.ws13.cara.dojo.backpressure.mongodb.util.RequestPaceSubscriber;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author ctranxuan
 */
public final class SubscriberWithRequestExample {
    private static Logger LOGGER = getLogger(SubscriberWithRequestExample.class);

    public static void main(String[] args) {
        MongoCollection<Document> collection = DojoUtil.createCollection();

        Flowable<Document> documentFlowable = Flowable.fromPublisher(collection.find());

        documentFlowable
                .doOnNext(d -> LOGGER.info("receive a document {}", d.get("name")))
                .subscribeWith(new RequestPaceSubscriber<>("arthur", 5000, 3));

        Flowable.timer(1, MINUTES)
                .blockingSubscribe();
    }
}
