package com.bigstep;

import rx.Observable;

/**
 * Created by alexandrubordei on 14/01/2016.
 */
public interface SongStore {
    /**
     * Search using index on artist_lc for a string that begins with something."
     * @param query
     * @return Observable of Songs that match the query
     */
    Observable<Song> getSongByArtistAsync(String query);
    /**
     * Returns a specific song by ID"
     * @param songID
     * @return Observable of Song that matches the query
     */
    Observable<Song> getSongByIDAsync(String songID);

}
