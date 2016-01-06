package com.bigstep.drivers;

import com.bigstep.Song;
import com.bigstep.SongStore;
import rx.Subscriber;
import rx.Subscription;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Created by alexandrubordei on 31/10/2015.
 */
public class FileSystemSongStore implements SongStore {
    private String _rootPath;

    public FileSystemSongStore() {
        this._rootPath = System.getProperty("com.bigstep.FileSystemSongStore.rootPath");
    }

    public Song getSong(String ID) throws Exception {
        Path path = getJsonPath(ID);
        return Song.createFromJson(new String(Files.readAllBytes(path)));
    }

    public Path getJsonPath(String ID) {
        String A = ID.substring(2, 3);
        String B = ID.substring(3, 4);
        String C = ID.substring(4, 5);
        return Paths.get(_rootPath, A, B, C, ID + ".json");
    }

    public Subscription searchSongAsync(String queryTerm, Subscriber subscriber) {
        return null;
    }

    @Override
    public List<Song> getSongByArtist(String artist) {
        return null;
    }

    public Subscription getSongByArtistAsync(String artist, Subscriber subscriber) {
        return null;
    }

    public Subscription getSongAsync(String id, Subscriber subscriber) {
        return null;
    }
    public List<Song> getSongSimilars(Song song){return null;}
}