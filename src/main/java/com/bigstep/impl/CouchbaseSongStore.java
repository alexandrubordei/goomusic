package com.bigstep.impl;

import com.bigstep.Song;
import com.bigstep.SongStore;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.view.AsyncViewResult;
import com.couchbase.client.java.view.ViewQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;


/**
 * Created by alexandrubordei on 14/01/2016.
 */
public class CouchbaseSongStore implements SongStore {

    public static final String SERVERS_PROPERTY = "com.bigstep.impl.CouchbaseSongStore.cbServers";
    public static final String BUCKET_NAME_PROPERTY = "com.bigstep.impl.CouchbaseSongStore.bucketName";
    public static final String PASSWORD_PROPERTY = "com.bigstep.impl.CouchbaseSongStore.password";
    private final static Logger logger = LoggerFactory.getLogger(CouchbaseSongStore.class);
    private Cluster cluster;
    private Bucket bucket;


    public CouchbaseSongStore() {
        String cbServers = System.getProperty(SERVERS_PROPERTY, "localhost");
        String bucketName = System.getProperty(BUCKET_NAME_PROPERTY, "default");
        String password = System.getProperty(PASSWORD_PROPERTY, "");

        cluster = CouchbaseCluster.create(cbServers);
        bucket = cluster.openBucket(bucketName, password);
    }

    @Override
    public Observable<Song> getSongByArtistAsync(String query) {
        return bucket
                .async()
                .query(ViewQuery.from("song", "artist").startKey(query).endKey(query + "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"))
                .limit(100)
                .flatMap(AsyncViewResult::rows)
                .map(r -> Song.createFromJson(r.value().toString()));
    }

    @Override
    public Observable<Song> getSongByIDAsync(String songID) {
        return bucket
                .async()
                .get(songID)
                .map(s -> Song.createFromJson(s.content().toString()));
    }


}
