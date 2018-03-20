package org.ws13.cara.dojo.backpressure.mongodb.util;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.reactivex.Flowable;
import org.bson.Document;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * @author ctranxuan
 */
public final class DojoUtil {

    private DojoUtil() {}

    public static MongoCollection<Document> createCollection() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost");
        MongoDatabase database = mongoClient.getDatabase("mydb");

        MongoCollection<Document> collection = database.getCollection("test");

        List<Document> documents = IntStream.range(0, 10000)
                                            .mapToObj(DojoUtil::makeDoc)
                                            .collect(toList());

        Flowable.fromPublisher(collection.insertMany(documents))
                .blockingSubscribe();

        return collection;
    }



    private static Document makeDoc(int aIndex) {
        return new Document("name", "doc-" + aIndex)
                        .append("type", "database")
                        .append("count", aIndex)
                        .append("info", new Document("x", 203).append("y", 102));

    }
}
