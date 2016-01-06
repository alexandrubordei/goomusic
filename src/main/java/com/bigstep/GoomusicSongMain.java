package com.bigstep;

import io.advantageous.qbit.http.server.HttpServer;
import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.advantageous.qbit.http.websocket.WebSocket;

import java.util.function.Consumer;

import static io.advantageous.qbit.http.server.HttpServerBuilder.httpServerBuilder;

/**
 * The main entrypoint of this microservice
 */
public class GoomusicSongMain {
    private final static Logger logger = LoggerFactory.getLogger(GoomusicSongMain.class);

    /**
     * Instantiates a new SongStore backend depending on the system property com.bigstep.GoomusicSongMain.songStoreImpl
     * It defaults to the com.bigstep.drivers.DummySongStore
     */
    @SuppressWarnings("unchecked")
    public static SongStore getSongStore() throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        String className = System.getProperty(
                "com.bigstep.GoomusicSongMain.songStoreImpl",
                "com.bigstep.drivers.DummySongStore");

        logger.debug("getSongStore(): com.bigstep.GoomusicSongMain.songStoreImpl="+className);

        Class<SongStore> clazz = (Class<SongStore>) Class.forName(className);

        return clazz.newInstance();
    }

    public static void main(final String... args) throws IllegalAccessException, InstantiationException, ClassNotFoundException {

        SongStore backendStore=getSongStore();
        logger.info("Initialised backend store to " + backendStore.getClass().getCanonicalName());
        SongService service = new SongService(backendStore);
        service.start();
    }
}
