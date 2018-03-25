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
public final class ShareWithDifferentPaceSubscribersExample {

    public static void main(String[] args) {
//        example1();
        example2();
//        example3();
    }

    private static void example1() {
        MongoCollection<Document> collection = DojoUtil.createCollection();

        Flowable<Document> sharedFlowable = Flowable.fromPublisher(collection.find())
                                                    .share();

        sharedFlowable
                .subscribeWith(new RequestPaceSubscriber<>("ford", 1, Long.MAX_VALUE));

        sharedFlowable
                .subscribeWith(new RequestPaceSubscriber<>("arthur", 1000, 1));

        Flowable.timer(10, MINUTES)
                .blockingSubscribe();
    }

    private static void example2() {
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

    private static void example3() {
        MongoCollection<Document> collection = DojoUtil.createCollection();

        Flowable<Document> sharedFlowable = Flowable.fromPublisher(collection.find())
                .observeOn(computation())
                .share();

        sharedFlowable
                .subscribeWith(new RequestPaceSubscriber<>("ford", 1, Long.MAX_VALUE));

        sharedFlowable
                .subscribeWith(new RequestPaceSubscriber<>("arthur", 1000, 1));

        Flowable.timer(10, MINUTES)
                .blockingSubscribe();
    }
}
