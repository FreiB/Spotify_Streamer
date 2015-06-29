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
import retrofit.RetrofitError;

/**
 * Created by Nir on 27/06/2015.
 * Singleton class for interacting with the Spotify API wrapper.
 */
public class Spotify {
    public static final String LOG_TAG = Spotify.class.getSimpleName();
    private static Spotify ourInstance = new Spotify();

    private SpotifyService mService;

    private final int THUMBNAIL_SIZE = 250;


    public static Spotify getInstance() {
        return ourInstance;
    }

    private Spotify() {
        SpotifyApi api = new SpotifyApi();
        mService = api.getService();

    }


    public List<DataEntity> SearchArtist(String name) {
        ArrayList<DataEntity> resultsArray = new ArrayList<>();
        try {
            ArtistsPager results = mService.searchArtists(name);
            for (Artist artist : results.artists.items) {
                String imageUrl = getImageUrl(artist.images, THUMBNAIL_SIZE);
                resultsArray.add(new DataEntity(artist.name, null, imageUrl, artist.id));
            }
        } catch (RetrofitError ex) {

        }
        return resultsArray;
    }

    public List<DataEntity> getTopTracks(String id) {
        ArrayList<DataEntity> resultsArray = new ArrayList<>();
        HashMap<String, Object> countryMap = new HashMap<>();
        countryMap.put("country", "US");
        try {
            Tracks tracks = mService.getArtistTopTrack(id, countryMap);
            for (Track track : tracks.tracks) {
                String imageUrl = getImageUrl(track.album.images, THUMBNAIL_SIZE);
                resultsArray.add(new DataEntity(track.album.name, track.name, imageUrl, track.id));
            }
        }catch (RetrofitError ex) {

        }
        return resultsArray;
    }

    private String getImageUrl(List<Image> images, int maxSize) {
        if (!images.isEmpty()) {
            for (Image image : images) {
                if (image.height <= maxSize && image.width <= maxSize)
                    return image.url;
            }
            return images.get(images.size()-1).url;
        }
        else return "";
    }

}
