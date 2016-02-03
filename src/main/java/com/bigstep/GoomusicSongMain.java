package com.bigstep;


import com.bigstep.impl.CouchbaseSongStore;
import com.bigstep.impl.MongoSongStore;
import io.vertx.core.Vertx;
import io.vertx.rx.java.RxHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.plugins.RxJavaSchedulersHook;

/**
 * The main entrypoint of this microservice
 */
public class GoomusicSongMain {
    private final static Logger logger = LoggerFactory.getLogger(GoomusicSongMain.class);



    public static void main(final String... args) throws IllegalAccessException, InstantiationException, ClassNotFoundException {


        Vertx vertx = Vertx.vertx();

        //need this for our use of rxjava.
        RxJavaSchedulersHook hook = RxHelper.schedulerHook(vertx);
        rx.plugins.RxJavaPlugins.getInstance().registerSchedulersHook(hook);

        String songStoreImpl = System.getProperty("com.bigstep.GoomusicSongMain.songStoreImpl","com.bigstep.impl.CouchbaseSongStore");
        Class<?> clazz = Class.forName(songStoreImpl);
        SongStore songStore = (SongStore)clazz.newInstance();

       //SongStore songStore = new CouchbaseSongStore();
       // SongStore songStore = new MongoSongStore();

        vertx.deployVerticle(new SongService(songStore));

        logger.debug("All verticles deployed.");
    }
}
