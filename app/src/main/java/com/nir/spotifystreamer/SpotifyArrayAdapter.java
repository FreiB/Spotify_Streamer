package com.nir.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class SpotifyArrayAdapter extends ArrayAdapter<SpotifyArrayAdapter.DataEntity> {
    public static final String LOG_TAG = SpotifyArrayAdapter.class.getSimpleName();

    public static final int ADAPTER_TYPE_SEARCH = 0;
    public static final int ADAPTER_TYPE_TOPTRACKS = 1;

    private int mAdapterType;

    public SpotifyArrayAdapter(Context context, int resource, int adapterType) {
        super(context, resource);
        mAdapterType = adapterType;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        ViewHolder holder;
        if (rootView == null) {
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder(rootView);
            rootView.setTag(holder);
        }
        else {
            holder = (ViewHolder)rootView.getTag();
        }

        switch (mAdapterType) {
            case ADAPTER_TYPE_SEARCH:
                adaptView(holder, position);
                holder.textView2.setVisibility(View.GONE);
                break;
            case ADAPTER_TYPE_TOPTRACKS:
                adaptView(holder,position);
                holder.textView2.setVisibility(View.VISIBLE);
                break;

        }
        return rootView;

    }

    private void adaptView(ViewHolder holder, int position) {
        DataEntity data = getItem(position);
        holder.textView1.setText(data.mText1);
        holder.textView2.setText(data.mText2);
        if (!data.mTmageUrl.isEmpty()) {
            Picasso.with(getContext()).load(data.mTmageUrl).into(holder.iconView);
        }
        else {
            holder.iconView.setImageResource(R.mipmap.ic_launcher);
        }

    }

    /**
     * ViewHolder class for reducing findViewById calls
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView textView1;
        public final TextView textView2;

        public ViewHolder (View view) {
            iconView = (ImageView)view.findViewById(R.id.list_item_icon_imageview);
            textView1 = (TextView)view.findViewById(R.id.list_item_textview1);
            textView2 = (TextView)view.findViewById(R.id.list_item_textview2);
        }
    }

    public static class DataEntity {

        public final String mID;
        public final String mText1;
        public final String mText2;
        public final String mTmageUrl;

        public DataEntity (String text1, String text2, String imageUrl, String id) {
            mID = id;
            mText1 = text1;
            mText2 = text2;
            mTmageUrl = imageUrl;
        }
    }

}
