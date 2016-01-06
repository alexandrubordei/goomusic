import com.bigstep.drivers.FileSystemSongStore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.bigstep.*;

/**
 * Created by alexandrubordei on 31/10/2015.
 */
public class TestFileSystemSongStore  {

    private String getResource(String name) throws URISyntaxException, IOException {
        return new String(Files.readAllBytes(Paths.get(this.getClass().getResource(name).toURI())));
    }

    @Test
    public void testFileSystemSongStore() throws Exception {
        URL rootURL=this.getClass().getResource("/A/A/A/TRAAAAW128F429D538.json");

        assertNotNull(rootURL);

        Path rootPath=Paths.get(rootURL.getPath());
        String rootPathStr=rootPath
                .getParent()
                .getParent()
                .getParent()
                .getParent()
                .toString();

        System.setProperty("com.bigstep.FileSystemSongStore.rootPath",rootPathStr);

        FileSystemSongStore store = new FileSystemSongStore();
        Song song=store.getSong("TRAAAAW128F429D538");

        assertEquals("Casual",song.artist);
        assert(false==song.similars.isEmpty());
        assertEquals("I Didn't Mean To",song.title);

    }

    @Test
    public void testFileSystemSongGetJsonPath() throws Exception {
        Path root=Paths.get(this.getClass().getResource("A/A/A/TRAAAAW128F429D538.json").getPath());
        String rootStr=root.getParent().toString();

        System.setProperty("com.bigstep.FileSystemSongStore.rootPath",rootStr);
        FileSystemSongStore store = new FileSystemSongStore();

        Path path=store.getJsonPath("TRAAAAW128F429D538");
        assertEquals(rootStr + "/A/A/A/TRAAAAW128F429D538.json",path.toString());


    }
}
