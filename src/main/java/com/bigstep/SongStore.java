package com.bigstep;

import rx.Subscriber;
import rx.Subscription;

import java.util.List;

/**
 * The database interface that the service uses
 * to talk to the database.
 */
public interface SongStore {
    Song getSong(String strSongID) throws Exception;
    Subscription getSongAsync(String id, Subscriber subscriber);
    Subscription searchSongAsync(String queryTerm, Subscriber subscriber);
    Subscription getSongByArtistAsync(String artist, Subscriber<Song> subscriber);
    List<Song> getSongSimilars(Song song);
}


