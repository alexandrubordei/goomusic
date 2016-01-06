package com.bigstep.drivers;

import com.bigstep.Song;
import com.bigstep.SongStore;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.view.DefaultAsyncViewResult;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * Created by alexandrubordei on 31/10/2015.
 */
public class CouchbaseSongStore implements SongStore {

    private Cluster cluster=null;
    private Bucket bucket=null;
    private final static Logger logger = LoggerFactory.getLogger(CouchbaseSongStore.class);

    public CouchbaseSongStore()
    {

        String cbServers  = System.getProperty("com.bigstep.CouchbaseSongStore.cbServers","localhost");
        String bucketName = System.getProperty("com.bigstep.CouchbaseSongStore.bucketName","default");
        String password   = System.getProperty("com.bigstep.CouchbaseSongStore.password","");

        cluster = CouchbaseCluster.create(cbServers);
        bucket = cluster.openBucket(bucketName, password);

    }

    public Song getSong(String strSongID)
    {
        JsonDocument json=bucket.get(strSongID);
        return Song.createFromJson(json.content().toString());
    }

    public Subscription searchSongAsync(String queryTerm, Subscriber subscriber) {

        String statement = "select * from " + bucket.name() + " where title like %" + queryTerm + "% or artist like %" + queryTerm + "%";
        N1qlQuery query = N1qlQuery.simple(statement);

        return bucket
                .async()
                .query(query)
                .subscribe(subscriber);
    }

    public Subscription getSongByArtistAsync(String artist, Subscriber<Song> subscriber) {

        Subscriber wrapperSubscriber= new Subscriber() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable throwable) {
                subscriber.onError(throwable);
            }

            @Override
            public void onNext(Object o) {
                DefaultAsyncViewResult result= (DefaultAsyncViewResult)o;
                if(result.success()) {
                    logger.debug("Subscriber.onNext");
                    result.rows().forEach(
                            row -> {
                                Song song=Song.createFromJson(row.value().toString());
                                logger.debug("Subscriber.onNext.rows.foreach title="+song.title);
                                subscriber.onNext(song);
                            },
                            error -> {
                                logger.error(error.getMessage());
                                error.printStackTrace();
                            }
                    );
                }

            }
        };

        return bucket
                .async()
                .query(ViewQuery.from("song","artist").key(artist))
                .subscribe(wrapperSubscriber);
    }


    public List<Song> getSongByArtist(String artist) {

        List<Song> list=new ArrayList<Song>();

        ViewResult result=bucket.query(ViewQuery.from("song","artist").key(artist));

        for (ViewRow row : result)
            list.add(Song.createFromJson(row.value().toString()));

        return list;
    }

    public Subscription getSongAsync(String id, Subscriber subscriber)
    {

        return  bucket
                    .async()
                    .get(id)
                .subscribe(subscriber);
    }


    public List<Song> getSongSimilars(Song song)
    {
       return Observable
                .from(song.similars)
                .flatMap(new Func1<ArrayList<String>, Observable<JsonDocument>>() {
                    @Override
                    public Observable<JsonDocument> call(ArrayList<String> similarPair) {
                        return bucket.async().get(similarPair.get(0));
                    }
                })
               .map(jsonDocument -> Song.createFromJson(jsonDocument.content().toString()))
               .toList()
               .toBlocking()
               .single();

    }


}
