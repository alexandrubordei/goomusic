package com.bigstep;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;


/**
 * Created by alexandrubordei on 02/11/2015.
 */
public class SongService extends AbstractVerticle {

    private final static Logger logger = LoggerFactory.getLogger(SongService.class);
    private final static String WEBSOCKET_PORT = "com.bigstep.SongService.port";
    public  final static String COMPLETED = "------------completed--------";
    int port;
    private HttpServer httpServer;
    private SongStore songStore;

    public SongService(SongStore songStore) {

        port = Integer.parseInt(System.getProperty(WEBSOCKET_PORT, "15000"));
        this.songStore = songStore;
    }

    public HttpServer setHttpServer(HttpServer httpServer) {
        return this.httpServer = httpServer;
    }

    public int getPort() {
        return port;
    }

    public void handleWebsocketMessage(ServerWebSocket ws, Buffer r) {
        String query = r.getString(0, r.length());

        logger.debug("Received:" + query);

        Observable<Song> songsWithThisArtist = songStore.getSongByArtistAsync(query);
        songsWithThisArtist.subscribe(s -> ws.write(Buffer.buffer(s.toJson())));

        /*Observable<Song> similars = songsWithThisArtist
                .flatMap(s -> Observable.from(s.similars))
                .limit(100)
                .distinct()
                .flatMap(s -> songStore.getSongByIDAsync(s.get(0)));

        similars.subscribe(new Subscriber<Song>() {
                               @Override
                               public void onCompleted() {
                                   ws.write(Buffer.buffer(COMPLETED));
                               }

                               @Override
                               public void onError(Throwable throwable) {

                               }

                               @Override
                               public void onNext(Song s) {
                                    ws.write(Buffer.buffer(s.toJson()));
                               }
                           });
         */
    }

    /**
     * start verticle and listen for websocket connections and requests.
     * Submit query on the event bus when received and reply with the response directly.
     * <p>
     * Initialises the httpServer if not already configured (by unit testing)
     */
    @Override
    public void start() {


        if (httpServer == null)
            httpServer = vertx.createHttpServer();

        httpServer.websocketHandler(ws -> ws.handler(r -> handleWebsocketMessage(ws, r)));

        httpServer.listen(port);
        logger.info("Started websocket verticle on port " + port);
    }

    @Override
    public void stop() {
        httpServer.close();
    }


}
