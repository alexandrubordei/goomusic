import com.bigstep.Song;
import com.bigstep.SongService;
import com.bigstep.SongStore;


import io.advantageous.qbit.http.client.HttpClient;
import io.advantageous.qbit.http.server.HttpServer;
import io.advantageous.qbit.http.websocket.WebSocket;



import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Subscriber;



import static io.advantageous.qbit.http.client.HttpClientBuilder.httpClientBuilder;
import static io.advantageous.qbit.http.server.HttpServerBuilder.httpServerBuilder;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import javax.websocket.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


import static org.mockito.Mockito.*;

/**
 * Created by alexandrubordei on 01/11/2015.
 */
public class TestSongService {


    static boolean executedTestCreateWebsocketConsumer=false;

    public ArrayList getMockSimilarArtists()
    {
        //prepare the songs to be delivered by the get similars
        ArrayList similars=new ArrayList();
        Song s1=new Song("lahlah","asd","asd","aa",null);
        similars.add(s1);
        Song s2=new Song("blabla","asd","asd","aa",null);
        similars.add(s2);

        return similars;

    }

    public Song createMockSong()
    {
        //create a specific song to be returned by the async method
        Song song=new Song();
        song.artist="dummy artist";
        song.similars=new ArrayList();
        song.similars.add(new ArrayList<>(Arrays.asList("TTTAAACCC","0.23323234")));
        song.similars.add(new ArrayList<>(Arrays.asList("TTTAAACCCAASS11","0.4523234")));

        return song;
    }

    @Before
    public void prepareServer() throws InterruptedException {
        //mock song store backend
        SongStore songStore = mock(SongStore.class);
        Song song = createMockSong();

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


        ArrayList similarArtists = getMockSimilarArtists();
        //execute the subscriber callback and submit the song upon async exec
        when(songStore.getSongSimilars(anyObject())).thenReturn(similarArtists);

        //instantiate the service using the 'special' songStore
        SongService service  = new SongService(songStore);

        HttpServer httpServer = httpServerBuilder()
                .setPort(32000)
                .build();

        service.createServer(httpServer).start();


        Thread.sleep(5000);

    }

    @Test
    public void testCreateWebsocketConsumer() throws IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, DeploymentException, InterruptedException {

        ArrayList similarArtists = getMockSimilarArtists();
        Song song = createMockSong();

        //Create a websocket client to port 15000
        HttpClient httpClient = httpClientBuilder().setPort(32000).build();
        httpClient.startClient();
        WebSocket webSocket = httpClient.createWebSocket("/");



        webSocket.setTextMessageConsumer(x->{
            System.out.println("received="+x);
            Song receivedSong=Song.createFromJson(x);

            Song s1=(Song)similarArtists.get(0);
            Song s2=(Song)similarArtists.get(1);

            assert( receivedSong.artist.equals(song.artist)||
                    receivedSong.artist.equals(s1.artist)||
                    receivedSong.artist.equals(s2.artist));

            System.out.println("received "+receivedSong.artist);

            this.executedTestCreateWebsocketConsumer=true;

        } );

        webSocket.openAndWait();


     /* Send some messages. */
        webSocket.sendText("anything");
     //   webSocket.sendText("anything");
     //   webSocket.sendText("anything");
      //  webSocket.sendText("anything");

        Thread.sleep(1000);

        assertEquals(true, this.executedTestCreateWebsocketConsumer);



        ///Sys.sleep(1000);
        webSocket.close();

        //verify(songStore).getSongSimilars(anyObject());

    }
}
