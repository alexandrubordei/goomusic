import com.bigstep.Song;
import com.bigstep.SongService;
import com.bigstep.SongStore;

import io.advantageous.boon.core.Sys;
import io.advantageous.qbit.http.client.HttpClient;
import io.advantageous.qbit.http.websocket.WebSocket;
import io.advantageous.qbit.server.ServiceEndpointServer;



import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Subscriber;
import rx.Subscription;

import static io.advantageous.qbit.http.client.HttpClientBuilder.httpClientBuilder;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by alexandrubordei on 01/11/2015.
 */
public class TestSongService {

    @Test
    public void testCreateWebsocketConsumer() throws IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, DeploymentException {

        //mock song store backend
        SongStore songStore = mock(SongStore.class);

        //create a specific song to be returned by the async method
        Song song=new Song();
        song.artist="dummy artist";
        song.similars=new ArrayList();
        song.similars.add(new ArrayList<>(Arrays.asList("TTTAAACCC","0.23323234")));
        song.similars.add(new ArrayList<>(Arrays.asList("TTTAAACCCAASS11","0.4523234")));

        //execute the subscriber callback and submit the song upon async exec
        when(songStore.getSongByArtistAsync(anyString(), anyObject())).then(
                new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Subscriber<Song> s=(Subscriber<Song>)invocation.getArguments()[1] ;
                        s.onNext(song);
                        return null;
                    }
                }
        );


        //prepare the songs to be delivered by the get similars
        ArrayList similars=new ArrayList();
        Song s1=new Song("lahlah","asd","asd","aa",null);
        similars.add(s1);
        Song s2=new Song("blabla","asd","asd","aa",null);
        similars.add(s2);

        //execute the subscriber callback and submit the song upon async exec
        when(songStore.getSongSimilars(anyObject())).thenReturn(similars);




        //instantiate the service using the 'special' songStore
        SongService service  = new SongService(songStore);
        service.createServer().start();
        Sys.sleep(1000);


        //websockets client

        HttpClient httpClient = httpClientBuilder()
                .setPort(15000).build();
        httpClient.startClient();

        WebSocket webSocket = httpClient
                .createWebSocket("/");


        webSocket.setTextMessageConsumer(x->{
            System.out.println(x);
            Song receivedSong=Song.createFromJson(x);


            assert( receivedSong.artist.equals(song.artist)||
                    receivedSong.artist.equals(s1.artist)||
                    receivedSong.artist.equals(s2.artist));

        } );
        webSocket.openAndWait();

        /* Send some messages. */
        webSocket.sendText("anything");

        Sys.sleep(1000);
        webSocket.close();

        verify(songStore).getSongSimilars(anyObject());

    }
}
