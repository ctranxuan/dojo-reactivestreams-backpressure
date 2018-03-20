package org.ws13.cara.dojo.backpressure.mongodb;

import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.Flowable;
import org.bson.Document;
import org.ws13.cara.dojo.backpressure.mongodb.util.DojoUtil;
import org.ws13.cara.dojo.backpressure.mongodb.util.PaceSubscriber;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author ctranxuan
 */
public final class TwoSubscribersExample {

    public static void main(String[] args) {
        MongoCollection<Document> collection = DojoUtil.createCollection();

        Flowable<Document> documentFlowable = Flowable.fromPublisher(collection.find());

        documentFlowable
                .subscribeWith(new PaceSubscriber<>("ford", 500));

        documentFlowable
                .subscribeWith(new PaceSubscriber<>("arthur", 10000));

        Flowable.timer(1, MINUTES)
                .blockingSubscribe();
    }
}
