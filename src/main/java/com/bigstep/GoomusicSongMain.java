package com.bigstep;


import io.vertx.core.Vertx;
import io.vertx.rx.java.RxHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.plugins.RxJavaSchedulersHook;

/**
 * The main entrypoint of this microservice
 */
public class GoomusicSongMain {
    public static final String SONG_STORE_IMPL_PROPERTY = "com.bigstep.GoomusicSongMain.songStoreImpl";
    private final static Logger logger = LoggerFactory.getLogger(GoomusicSongMain.class);

    public static void main(final String... args) throws IllegalAccessException, InstantiationException, ClassNotFoundException {


        Vertx vertx = Vertx.vertx();

        //This is needed so that rx.java uses the vertex's event bus instead of it's own.
        RxJavaSchedulersHook hook = RxHelper.schedulerHook(vertx);
        rx.plugins.RxJavaPlugins.getInstance().registerSchedulersHook(hook);

        //Instantiate the backend specified by the system property
        String songStoreImpl = System.getProperty(SONG_STORE_IMPL_PROPERTY, "com.bigstep.impl.CouchbaseSongStore");
        Class<?> clazz = Class.forName(songStoreImpl);
        SongStore songStore = (SongStore) clazz.newInstance();

        vertx.deployVerticle(new SongService(songStore));

        logger.debug("All verticles deployed.");
    }
}
