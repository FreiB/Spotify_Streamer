package com.nir.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;



public class TopTracksFragment extends Fragment {
    public static final String LOG_TAG = TopTracksFragment.class.getSimpleName();

    private SpotifyArrayAdapter mAdapter;

    public TopTracksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        mAdapter = new SpotifyArrayAdapter(
                getActivity(),
                0,
                SpotifyArrayAdapter.ADAPTER_TYPE_TOPTRACKS);
        ListView listView = (ListView) rootView.findViewById(R.id.top_tracks_listview);
        listView.setAdapter(mAdapter);
        new FetchTopTracksTask().execute(getActivity().getIntent().getStringExtra(
                getString(R.string.artist_id)));
        return rootView;
    }

    public class FetchTopTracksTask extends AsyncTask<String, Void, List<SpotifyArrayAdapter.DataEntity>> {

        @Override
        protected List<SpotifyArrayAdapter.DataEntity> doInBackground(String... strings) {
            return Spotify.getInstance().getTopTracks(strings[0]);
        }

        @Override
        protected void onPostExecute(List<SpotifyArrayAdapter.DataEntity> dataEntities) {
            if (dataEntities.size() == 0) {
                Toast.makeText(getActivity(),
                        R.string.no_top_tracks_text,
                        Toast.LENGTH_SHORT).show();
            }
            mAdapter.addAll(dataEntities);
        }
    }
}
