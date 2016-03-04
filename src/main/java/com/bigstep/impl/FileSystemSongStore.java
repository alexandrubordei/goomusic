package com.bigstep.impl;

import com.bigstep.Song;
import com.bigstep.SongStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alexandrubordei on 24/01/2016.
 */
public class FileSystemSongStore implements SongStore {


    public static final String ROOT_PATH_PROPERTY = "com.bigstep.impl.FileSystemSongStore.root";
    private final static Logger logger = LoggerFactory.getLogger(FileSystemSongStore.class);
    private String rootPath;


    public FileSystemSongStore() {
        Path currentRelativePath = Paths.get("");
        String defaultRoot = currentRelativePath.toAbsolutePath().toString();
        rootPath = System.getProperty(ROOT_PATH_PROPERTY, defaultRoot);
        logger.info("Initialised FileSystemSongStore with rootPath=" + rootPath);
    }

    public Path getJsonPath(String ID) {
        String A = ID.substring(2, 3);
        String B = ID.substring(3, 4);
        String C = ID.substring(4, 5);
        return Paths.get(rootPath, A, B, C, ID + ".json");
    }

    public Song getSongByID(String songID) {
        Path path = getJsonPath(songID);
        return getSongAtPath(path);
    }

    public Song getSongAtPath(Path path) {
        try {
            return Song.createFromJson(new String(Files.readAllBytes(path)));
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("getSongAtPath Exception" + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Scans the entire database for matching artists
     *
     * @param query
     * @return
     */
    @Override
    public Observable<Song> getSongByArtistAsync(String query) {

        String artist = query.toLowerCase().replaceAll("\\s", "");

        ArrayList<File> files = new ArrayList<>();
        try {
            Files.walk(Paths.get(rootPath)).forEach(f -> files.add(new File(f.toUri())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Observable.from(files)
                .filter(file -> !file.isDirectory())
                .map(file -> getSongAtPath(Paths.get(file.getPath())))
                .filter(song -> song.artist.toLowerCase().replaceAll("\\s", "").equals(artist));
    }

    @Override
    public Observable<Song> getSongByIDAsync(String songID) {
        return Observable.from(Arrays.asList(getSongByID(songID)));
    }
}
