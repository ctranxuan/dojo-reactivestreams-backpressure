package org.ws13.cara.dojo.backpressure.mongodb;

import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.Flowable;
import org.bson.Document;
import org.ws13.cara.dojo.backpressure.mongodb.util.DojoUtil;
import org.ws13.cara.dojo.backpressure.mongodb.util.RequestPaceSubscriber;

import static io.reactivex.schedulers.Schedulers.computation;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author ctranxuan
 */
public final class HotFlowableWithDifferentPaceSubscribersExample {

    public static void main(String[] args) {
        MongoCollection<Document> collection = DojoUtil.createCollection();

        Flowable<Document> sharedFlowable = Flowable.fromPublisher(collection.find())
                                                    .share();

        sharedFlowable
                .observeOn(computation())
                .subscribeWith(new RequestPaceSubscriber<>("ford", 1, Long.MAX_VALUE));

        sharedFlowable
                .observeOn(computation())
                .subscribeWith(new RequestPaceSubscriber<>("arthur", 1000, 1));

        Flowable.timer(10, MINUTES)
                .blockingSubscribe();
    }
}
