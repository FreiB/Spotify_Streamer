package com.nir.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;



public class SearchFragment extends Fragment {
    public static final String LOG_TAG = SearchFragment.class.getSimpleName();

    SpotifyArrayAdapter mAdapter;
    SearchArtistTask mSearchTask;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchTask = new SearchArtistTask();
        mAdapter = new SpotifyArrayAdapter(getActivity(),0,SpotifyArrayAdapter.ADAPTER_TYPE_SEARCH);

        ListView listView = (ListView)rootView.findViewById(R.id.ListViewSearch);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SpotifyArrayAdapter.DataEntity data = mAdapter.getItem(i);
                Intent intent = new Intent(getActivity(), TopTracksActivity.class);
                intent.putExtra(getString(R.string.artist_id), data.mID);
                intent.putExtra(getString(R.string.artist_name), data.mText1);
                startActivity(intent);
            }
        });

        EditText searchBox = (EditText)rootView.findViewById(R.id.EditTextSearch);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    mAdapter.clear();
                    return;
                }
                if (!mSearchTask.isCancelled()) {
                    mSearchTask.cancel(true);
                    mSearchTask = new SearchArtistTask();
                }
                mSearchTask.execute(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return rootView;
    }

    /**
     * Created by Nir on 27/06/2015.
     */
    public class SearchArtistTask extends AsyncTask<String, Void, List<SpotifyArrayAdapter.DataEntity>> {

        @Override
        protected List<SpotifyArrayAdapter.DataEntity> doInBackground(String... strings) {
            return Spotify.getInstance().SearchArtist(strings[0]);
        }

        @Override
        protected void onPostExecute(List<SpotifyArrayAdapter.DataEntity> dataEntities) {
            if (dataEntities.size() == 0) {
                Toast.makeText(getActivity(),
                        getString(R.string.no_result_text),
                        Toast.LENGTH_SHORT).show();
            }
            mAdapter.clear();
            mAdapter.addAll(dataEntities);
        }
    }
}
