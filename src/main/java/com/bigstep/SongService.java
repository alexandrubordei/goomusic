package com.bigstep;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.view.AsyncViewRow;
import com.google.gson.Gson;
import io.advantageous.qbit.http.server.HttpServer;
import io.advantageous.qbit.http.websocket.WebSocket;
import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;
import io.advantageous.qbit.service.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.couchbase.client.java.view.DefaultAsyncViewResult;
import rx.functions.Action1;
import rx.functions.Func1;

import static io.advantageous.qbit.http.server.HttpServerBuilder.httpServerBuilder;

/**
 * Created by alexandrubordei on 02/11/2015.
 */
public class SongService {

    private final static Logger logger = LoggerFactory.getLogger(SongService.class);

    private final SongStore songStore;

    public SongService(SongStore sStore)
    {
        this.songStore = sStore;
    }

    public SongStore getSongStore()
    {
        return songStore;
    }

    private int getPort()
    {
        return Integer.parseInt(System.getProperty("com.bigstep.SongServiceFactory.port","15000"));
    }


    public HttpServer createServer() {

        SongStore songStore = getSongStore();


        //build the http server
        HttpServer httpServer = httpServerBuilder()
                .setPort(getPort())
                .build();

        httpServer.setWebSocketMessageConsumer(
                webSocketMessage -> {
                        //upon message arrival, perform search
                        Subscriber<Song> subscriber = new Subscriber<Song>()
                        {
                            @Override
                            public void onNext(Song song) {
                                //send this song
                                webSocketMessage.getSender().sendText(song.toJson());
                                //send it's similars
                                List<Song> similars=songStore.getSongSimilars(song);
                                logger.debug("+++++++++++++++Similars:"+similars.size());
                                for(Song similar: similars) {
                                    logger.debug("+++++++++++++++Similars:Sending"+similar.artist);
                                    webSocketMessage.getSender().sendText(similar.toJson());
                                }

                            }

                            @Override
                            public void onCompleted() { }

                            @Override
                            public void onError(Throwable e) {

                                webSocketMessage.getSender().sendText(e.getMessage());
                            };

                    };

                    songStore.getSongByArtistAsync(webSocketMessage.getMessage().toString(), subscriber);

                }
        );
        logger.debug("createServer(): Initialised, returning server");
        return httpServer;
    }


    public void start()
    {
        HttpServer server=this.createServer();
        server.start();
    }


}
