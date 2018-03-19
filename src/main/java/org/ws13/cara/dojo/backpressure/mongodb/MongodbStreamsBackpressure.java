package org.ws13.cara.dojo.backpressure.mongodb;

import com.mongodb.reactivestreams.client.MongoCollection;
import io.reactivex.Flowable;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.ws13.cara.dojo.backpressure.mongodb.util.MongodbUtil;

import java.util.stream.Collectors;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author ctranxuan
 */
public class MongodbStreamsBackpressure {
    private static Logger LOGGER = getLogger(MongodbStreamsBackpressure.class);

    public static void main(String[] args) {
        MongoCollection<Document> collection = MongodbUtil.createCollection();

        Flowable.fromPublisher(collection.find())
                .take(1)
                .blockingSubscribe(n -> LOGGER.info(n.entrySet()
                                                     .stream()
                                                     .map(e -> e.getKey() + ": " + e.getValue())
                                                     .collect(Collectors.joining(", "))));
    }
}
