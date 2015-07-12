package com.nir.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SpotifyArrayAdapter.DataEntity data =
                        (SpotifyArrayAdapter.DataEntity)adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra(getString(R.string.track_id), data.mID);
                intent.putExtra(getString(R.string.image_url), data.mTmageUrl);
                startActivity(intent);

            }
        });

        if (Utility.isNetworkAvailable(getActivity())) {
            new FetchTopTracksTask().execute(getActivity().getIntent().getStringExtra(
                    getString(R.string.artist_id)));
        }
        else {
            Toast.makeText(getActivity(),
                    R.string.connection_error,
                    Toast.LENGTH_SHORT);
        }

        return rootView;
    }

    public class FetchTopTracksTask extends AsyncTask<String, Void, List<SpotifyArrayAdapter.DataEntity>> {

        @Override
        protected List<SpotifyArrayAdapter.DataEntity> doInBackground(String... strings) {
            return Spotify.getInstance().getTopTracks(strings[0]);
        }

        @Override
        protected void onPostExecute(List<SpotifyArrayAdapter.DataEntity> dataEntities) {
            if (dataEntities != null) {
                if (dataEntities.size() == 0) {
                    Toast.makeText(getActivity(),
                            R.string.no_top_tracks_text,
                            Toast.LENGTH_SHORT).show();
                }
                mAdapter.addAll(dataEntities);
            }
        }
    }
}
