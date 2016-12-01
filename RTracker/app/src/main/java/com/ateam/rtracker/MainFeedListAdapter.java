package com.ateam.rtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Bre on 2016-11-28.
 */

public class MainFeedListAdapter extends ArrayAdapter<FeedItem> {

    public MainFeedListAdapter(Context context, int textViewResourceId, List<FeedItem> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        final int item = i;
        MainFeedListAdapter.ViewHolder holder;

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.content_main_feed_item, null);
            holder = new MainFeedListAdapter.ViewHolder();
            holder.desc = (TextView) view.findViewById(R.id.description);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.image = (ImageView) view.findViewById(R.id.imageView);
            view.setTag(holder);
        } else {
            holder = (MainFeedListAdapter.ViewHolder) view.getTag();
        }

        FeedItem feedItem = getItem(item);
        holder.title.setText(feedItem.title);
        holder.desc.setText(feedItem.content);

        if(feedItem.hasImage) {
            holder.image.setImageResource(feedItem.imageID);
        } else {
            holder.image.setImageResource(0);
        }

        return view;
    }

    static class ViewHolder {
        TextView title;
        TextView desc;
        ImageView image;
    }
}
