package com.bigstep;

import rx.Observable;

/**
 * Created by alexandrubordei on 14/01/2016.
 */
public interface SongStore {
    Observable<Song> getSongByArtistAsync(String artist);
    Observable<Song> getSongByIDAsync(String songID);

}
