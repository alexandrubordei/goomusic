package com.bigstep.impl;

import com.bigstep.Song;
import com.bigstep.SongStore;
import com.mongodb.ConnectionString;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import static com.mongodb.client.model.Filters.*;


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

    public MongoSongStore() {
        String connectionString = System.getProperty(CONNECTION_STRING_PROPERTY, "mongodb://localhost");
        String databaseName = System.getProperty(DATABASE_NAME_PROPERTY, "default");
        String collectionName = System.getProperty(COLLECTION_NAME_PROPERTY, "default");

        mongoClient = MongoClients.create(new ConnectionString(connectionString));
        mongoDatabase = mongoClient.getDatabase(databaseName);
        mongoCollection = mongoDatabase.getCollection(collectionName);

        logger.info("Initialized Mongo Driver with databaseName=" + databaseName + " collectionName=" + collectionName);

    }

    @Override
    public Observable<Song> getSongByArtistAsync(String query) {
        return mongoCollection
                .find(and(gte("artist_lc", query), lte("artist_lc", query + "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz")))
                .limit(100)
                .toObservable()
                .map(d -> Song.createFromJson(d.toJson()));
    }

    @Override
    public Observable<Song> getSongByIDAsync(String songID) {
        return mongoCollection
                .find(eq("track_id", songID))
                .toObservable()
                .map(d -> Song.createFromJson(d.toJson()));
    }
}
