package com.nir.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment {

    private ImageView mImage;

    public PlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        mImage = (ImageView)rootView.findViewById(R.id.player_album_image_imageview);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            String imageUrl = intent.getStringExtra(getString(R.string.image_url));
            Picasso.with(getActivity()).load(imageUrl).into(mImage);
        }
        return rootView;
    }

}
