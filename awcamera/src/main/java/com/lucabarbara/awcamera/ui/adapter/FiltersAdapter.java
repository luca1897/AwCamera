package com.lucabarbara.awcamera.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lucabarbara.awcamera.R;
import com.lucabarbara.awcamera.model.ThumbnailItem;
import com.lucabarbara.awcamera.ui.activity.AwCamera;
import com.lucabarbara.awcamera.ui.fragment.EffectsFragment;
import com.lucabarbara.awcamera.ui.fragment.GalleryFragment;
import com.squareup.picasso.Picasso;
import com.zomato.photofilters.imageprocessors.Filter;

import java.util.List;

/**
 * Created by luca1897 on 25/06/17.
 */

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.ViewHolder> {

    private LayoutInflater mInflater;

    private int selected = 0;
    private List<ThumbnailItem> images;

    private AwCamera awCamera;
    private ViewGroup parent;
    private EffectsFragment effectsFragment;


    public FiltersAdapter(EffectsFragment effectsFragment, List<ThumbnailItem> images) {
        this.awCamera = (AwCamera)effectsFragment.getActivity();
        this.effectsFragment = effectsFragment;
        this.images = images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.parent = parent;
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_effect_item, parent, false);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.filterName.setText(images.get(position).name);
        holder.imageView.setImageBitmap(images.get(position).image);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                effectsFragment.setImage(images.get(position).image);
                selected = position;
                notifyDataSetChanged();
            }
        });

        if(selected == position)
        {
            holder.filterName.setTextColor(Color.BLACK);
        }else{
            holder.filterName.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ImageView imageView;
        public TextView filterName;
        public ViewHolder(final View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            filterName = (TextView) v.findViewById(R.id.filter_name);
            view = v;
        }
    }

    public Filter getFilterSelected() {
        return images.get(selected).filter;
    }
}
