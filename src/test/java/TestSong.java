import com.bigstep.Song;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Created by alexandrubordei on 31/10/2015.
 */
public class TestSong {

    private String getResource(String name) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(this.getClass().getResource(name).toURI())));

    }

    @Test
    public void testFileSystemSongStore() throws Exception {

        Song song = Song.createFromJson(getResource("A/A/A/TRAAAAW128F429D538.json"));
        assertEquals("Casual", song.artist);
        assert (false == song.similars.isEmpty());
        assertEquals("I Didn't Mean To", song.title);
    }

}
