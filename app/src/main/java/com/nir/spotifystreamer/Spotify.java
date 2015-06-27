package com.nir.spotifystreamer;

import com.nir.spotifystreamer.SpotifyArrayAdapter.DataEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Nir on 27/06/2015.
 */
public class Spotify {
    public static final String LOG_TAG = Spotify.class.getSimpleName();
    private static Spotify ourInstance = new Spotify();

    private SpotifyService mService;

    public static Spotify getInstance() {
        return ourInstance;
    }

    private Spotify() {
        SpotifyApi api = new SpotifyApi();
        mService = api.getService();

    }

    public List<DataEntity> SearchArtist(String name) {
        ArrayList<DataEntity> resultsArray = new ArrayList<>();
        ArtistsPager results = mService.searchArtists(name);
        for (Artist artist : results.artists.items) {
            Image image = null;
            if (artist.images.size() > 0) {
                image = artist.images.get(artist.images.size() - 2);
            }
            String imageUrl = "";
            if (image != null)
                imageUrl = image.url;
            resultsArray.add(new DataEntity(artist.name, null, imageUrl, artist.id));
        }
        return resultsArray;
    }

    public List<DataEntity> getTopTracks(String id) {
        ArrayList<DataEntity> resultsArray = new ArrayList<>();
        HashMap<String, Object> countryMap = new HashMap<>();
        countryMap.put("country", "US");
        Tracks tracks = mService.getArtistTopTrack(id,countryMap);
        for (Track track : tracks.tracks)
        {
            Image image = null;
            if (track.album.images.size() > 0) {
                image = track.album.images.get(track.album.images.size() - 2);
            }
            resultsArray.add(new DataEntity(track.album.name, track.name, image.url, track.id));
        }
        return resultsArray;
    }
}
