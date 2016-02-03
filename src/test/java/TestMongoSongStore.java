import com.bigstep.Song;
import com.bigstep.impl.FileSystemSongStore;
import com.bigstep.impl.MongoSongStore;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by alexandrubordei on 24/01/2016.
 */
public class TestMongoSongStore {

    private MongoSongStore songStore;

    @Before
    public void setup()
    {

        System.setProperty(MongoSongStore.COLLECTION_NAME_PROPERTY,"songs");
        System.setProperty(MongoSongStore.DATABASE_NAME_PROPERTY,"lastfm_subset");
        songStore = new MongoSongStore();

    }

    @Test
    public void testGetSongByArtistAsync()
    {
        Observable<Song> songStream = songStore.getSongByArtistAsync("Casual");

        TestSubscriber<Song> ts= new TestSubscriber<>();
        songStream.subscribe(ts);

        songStream.subscribe( s -> System.out.println(s.toJson()));

        ts.awaitTerminalEvent();
        ts.assertCompleted();
        ts.assertValueCount(8);
    }

    @Test
    public void testGetSongByIDAsync()
    {
        Observable<Song> songStream = songStore.getSongByIDAsync("TRAAAAW128F429D538");

        TestSubscriber<Song> ts= new TestSubscriber<>();
        songStream.subscribe(ts);

        songStream.subscribe(
                s -> {
                    System.out.println(s.toJson());
                    assertEquals("Casual",s.artist);
                }
        );

        ts.awaitTerminalEvent();
        ts.assertCompleted();
        ts.assertValueCount(1);
    }



}
