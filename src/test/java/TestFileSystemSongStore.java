import com.bigstep.impl.FileSystemSongStore;
import com.bigstep.Song;
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
public class TestFilesystemSongStore {

    private FileSystemSongStore fsSongStore;

    @Before
    public void setup()
    {
        URL url=this.getClass().getResource("/A/A/A/TRAAAAW128F429D538.json");
        String root=Paths.get(url.getFile()).getParent().getParent().getParent().getParent().toString();
        System.setProperty(FileSystemSongStore.ROOT_PATH_PROPERTY,root);
        fsSongStore = new FileSystemSongStore();

    }

    @Test
    public void testGetSongByID()
    {
        Song song = fsSongStore.getSongByID("TRAAAAW128F429D538");
        assertEquals("Casual",song.artist);
    }

    @Test
    public void testGetSongByIDAsync()
    {
        Observable<Song> songStream = fsSongStore.getSongByIDAsync("TRAAAAW128F429D538");
        TestSubscriber<Song> ts= new TestSubscriber<>();
        songStream.subscribe(ts);
        List<Song> s= ts.getOnNextEvents();
        ts.assertCompleted();
        assertEquals("Casual", s.get(0).artist);
    }

    @Test
    public void testGetSongByArtistAsync()
    {
        Observable<Song> songStream = fsSongStore.getSongByArtistAsync("casual");
        TestSubscriber<Song> ts= new TestSubscriber<>();
        songStream.subscribe(ts);
        List<Song> s= ts.getOnNextEvents();
        ts.assertValueCount(1);
        ts.assertCompleted();
    }

}
