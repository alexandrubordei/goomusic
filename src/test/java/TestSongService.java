import com.bigstep.impl.FileSystemSongStore;
import com.bigstep.Song;
import com.bigstep.SongService;
import com.bigstep.SongStore;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rx.java.RxHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.plugins.RxJavaSchedulersHook;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


/**
 * Created by alexandrubordei on 01/11/2015.
 */
@RunWith(VertxUnitRunner.class)
public class TestSongService {

    SongStore songStore;

    Vertx vertx;

    @Before
    public void prepareFsSongStore() {
        vertx = Vertx.vertx();

        //need this for our use of rxjava.
        RxJavaSchedulersHook hook = RxHelper.schedulerHook(vertx);
        rx.plugins.RxJavaPlugins.getInstance().registerSchedulersHook(hook);

        URL url = this.getClass().getResource("/A/A/A/TRAAAAW128F429D538.json");
        String root = Paths.get(url.getFile()).getParent().getParent().getParent().getParent().toString();
        System.setProperty(FileSystemSongStore.ROOT_PATH_PROPERTY, root);
        songStore = new FileSystemSongStore();
    }


    @Test
    public void testSongService(TestContext context) throws IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, DeploymentException, InterruptedException {


        String queryTerm = "casual";
        List<String> expected = Arrays.asList("TRAAAAW128F429D538", "TRAAABD128F429CF47", "TRAAADZ128F9348C2E", "TRAAAMQ128F1460CD3");

        SongService service = new SongService(songStore);
        vertx.deployVerticle(service);

        Async async = context.async(expected.size()+1);

        HttpClient client = vertx.createHttpClient();
        client.websocket(service.getPort(), "localhost", "/", ws -> {
            ws.handler(b -> {

                String str = b.toString();

                System.out.println(str);

                if(str.equals(SongService.COMPLETED))
                    async.complete();
                else {
                    Song song = Song.createFromJson(b.toString());
                    System.out.println(song.track_id);

                    context.assertTrue(expected.contains(song.track_id));
                    async.countDown();
                }
            });
            ws.write(Buffer.buffer(queryTerm));
        });

        async.awaitSuccess();


    }

}
