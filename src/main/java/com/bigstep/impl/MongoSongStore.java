package com.bigstep.impl;

import com.bigstep.Song;
import com.bigstep.SongService;
import com.bigstep.SongStore;
import com.couchbase.client.java.view.AsyncViewResult;
import com.couchbase.client.java.view.AsyncViewRow;
import com.couchbase.client.java.view.ViewQuery;
import com.mongodb.ConnectionString;
import com.mongodb.client.model.TextSearchOptions;
import com.mongodb.rx.client.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Filters.text;


/**
 * Created by alexandrubordei on 14/01/2016.
 */
public class MongoSongStore implements SongStore {

    private final static Logger logger = LoggerFactory.getLogger(MongoSongStore.class);

    public static final String CONNECTION_STRING_PROPERTY = "com.bigstep.impl.MongoSongStore.connectionString";
    public static final String DATABASE_NAME_PROPERTY = "com.bigstep.impl.MongoSongStore.databaseName";
    public static final String COLLECTION_NAME_PROPERTY = "com.bigstep.impl.MongoSongStore.collectionName";
    public static final String PASSWORD_PROPERTY = "com.bigstep.impl.MongoSongStore.password";

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;

    public MongoSongStore()
    {
        String connectionString = System.getProperty(CONNECTION_STRING_PROPERTY, "mongodb://localhost");
        String databaseName = System.getProperty(DATABASE_NAME_PROPERTY, "default");
        String collectionName = System.getProperty(COLLECTION_NAME_PROPERTY, "default");

        mongoClient = MongoClients.create(new ConnectionString(connectionString));
        mongoDatabase = mongoClient.getDatabase(databaseName);
        mongoCollection = mongoDatabase.getCollection(collectionName);

        logger.info("Initialized Mongo Driver with databaseName="+databaseName+" collectionName="+collectionName);

    }

    public Observable<Song> getSongByArtistAsync(String artist) {

        return mongoCollection
                .find(regex("artist", "^(?i)"+Pattern.quote(artist)))
                .toObservable()
                .map(d -> Song.createFromJson(d.toJson()));
    }

    public Observable<Song> getSongByIDAsync(String songID)
    {
         return mongoCollection
                .find(eq("track_id",songID))
                .toObservable()
                .map(d -> Song.createFromJson(d.toJson()));
    }
}
