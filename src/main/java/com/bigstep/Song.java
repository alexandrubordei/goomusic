package com.bigstep;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;

import java.util.*;

/**
 * Created by alexandrubordei on 31/10/2015.
 */
public class Song {
    public String artist=null;
    public String timestamp=null;
    public ArrayList<ArrayList<String>> similars= new ArrayList<>();;
    public String track_id = null;
    public String title = null;

    public Song()
    {

    }
    public Song(String artist, String timestamp, String track_id, String title, ArrayList<ArrayList<String>> similars)
    {
        this.artist=artist;
        this.timestamp=timestamp;
        this.track_id=track_id;
        this.title=title;
        this.similars=similars;
    }

    public static Song createFromJson(String json)
    {
        Gson gson = new Gson();
        return gson.fromJson(json,Song.class);
    }

    public  String toJson()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


}

