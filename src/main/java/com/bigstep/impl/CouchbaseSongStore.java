package com.bigstep.impl;

import com.bigstep.Song;
import com.bigstep.SongStore;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.query.AsyncN1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.view.AsyncViewResult;
import com.couchbase.client.java.view.AsyncViewRow;
import com.couchbase.client.java.view.ViewQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;
import static com.couchbase.client.java.query.dsl.functions.StringFunctions.lower;


/**
 * Created by alexandrubordei on 14/01/2016.
 */
public class CouchbaseSongStore implements SongStore {

    private final static Logger logger = LoggerFactory.getLogger(CouchbaseSongStore.class);

    public static final String SERVERS_PROPERTY = "com.bigstep.impl.CouchbaseSongStore.cbServers";
    public static final String BUCKET_NAME_PROPERTY = "com.bigstep.impl.CouchbaseSongStore.bucketName";
    public static final String PASSWORD_PROPERTY = "com.bigstep.impl.CouchbaseSongStore.password";

    private Cluster cluster;
    private Bucket bucket;


    public CouchbaseSongStore()
    {
        String cbServers = System.getProperty(SERVERS_PROPERTY, "localhost");
        String bucketName = System.getProperty(BUCKET_NAME_PROPERTY, "default");
        String password = System.getProperty(PASSWORD_PROPERTY, "");

        cluster = CouchbaseCluster.create(cbServers);
        bucket = cluster.openBucket(bucketName, password);
    }



    public Observable<Song> getSongByArtistAsync(String query) {



        return bucket
            .async()
            .query(ViewQuery.from("song", "artist").startKey(query).endKey(query+"zzzzzzzzzzzzzzzzzzz"))
                .limit(100)
                .flatMap(AsyncViewResult::rows)

                .map(r -> Song.createFromJson(r.value().toString()));
    }
    /*
           return bucket
               .async()
               .query(select("*")
                        .from(i(bucket.name()))
                        .where(lower(x("artist")).like(s(query))
                        ))
                .limit(100)
                .flatMap(AsyncN1qlQueryResult::rows)
                .map(r -> Song.createFromJson(r.value().get(bucket.name()).toString()));

     */

    public Observable<Song> getSongByIDAsync(String songID)
    {
        return bucket
                .async()
                .get(songID)
                .map( s -> Song.createFromJson(s.content().toString()));
    }
}
