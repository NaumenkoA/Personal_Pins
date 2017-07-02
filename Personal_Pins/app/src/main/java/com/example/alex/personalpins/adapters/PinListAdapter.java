package com.example.alex.personalpins.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.alex.personalpins.R;
import com.example.alex.personalpins.model.MediaType;
import com.example.alex.personalpins.model.Pin;
import com.example.alex.personalpins.model.Tag;
import com.example.alex.personalpins.ui.ImageEnlargeActivity;
import com.example.alex.personalpins.ui.PlayVideoActivity;

import java.util.ArrayList;

public class PinListAdapter extends RecyclerView.Adapter<PinListAdapter.ViewHolder> {

    private ArrayList<Pin> pins;
    private Context context;
    private TextView emptyTextView;

    public PinListAdapter(ArrayList <Pin> pins, Context context, TextView emptyTextView) {
        this.pins = pins;
        this.context = context;
        this.emptyTextView = emptyTextView;
    }

    @Override
    public PinListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pin_item_layout, parent, false);
        return new PinListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pin pin = pins.get(position);
        Glide.with(context).load(pin.getUri()).into(holder.pinImageView);
        if (pin.getMediaType().equals(MediaType.MEDIA_TYPE_VIDEO))
            holder.playImageView.setVisibility(View.VISIBLE);
        holder.pinTitleTextView.setText(pin.getTitle());
        if (pin.getComment() != null && !pin.getComment().equals("")){
            holder.pinCommentTextView.setText(pin.getComment());
        } else holder.pinCommentTextView.setVisibility(View.GONE);
        if (pin.getTags() != null && pin.getTags().size() != 0){
            holder.setTags(pin.getTags());
        } else holder.pinTagsTextView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        emptyTextView.setVisibility(pins.size() > 0 ? View.GONE : View.VISIBLE);
        return (pins != null) ? pins.size():0;
    }

    public void refreshData(ArrayList<Pin> pins) {
        this.pins = pins;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView pinImageView;
        public ImageView playImageView;
        public TextView pinTitleTextView;
        public TextView pinCommentTextView;
        public TextView pinTagsTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            pinImageView = (ImageView) itemView.findViewById(R.id.pinImageView);
            pinTitleTextView = (TextView) itemView.findViewById(R.id.pinTitleTextView);
            pinCommentTextView = (TextView) itemView.findViewById(R.id.pinCommentTextView);
            pinTagsTextView = (TextView) itemView.findViewById(R.id.pinTagsTextView);
            playImageView = (ImageView) itemView.findViewById(R.id.playImageView);
            pinImageView.setOnClickListener(this);
            pinImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.pinImageView) {
                Pin pin = pins.get(getAdapterPosition());
                switch (pin.getMediaType()) {
                    case (MediaType.MEDIA_TYPE_IMAGE):
                        Intent intent = new Intent(context, ImageEnlargeActivity.class);
                        intent.setData(pin.getUri());
                        intent.putExtra(ImageEnlargeActivity.INTENT_SOURCE, context.toString());
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                                (Activity) context, this.pinImageView, "pinImage");
                        context.startActivity(intent, options.toBundle());
                        break;
                    case (MediaType.MEDIA_TYPE_VIDEO):
                        Intent playVideoIntent = new Intent(context, PlayVideoActivity.class);
                        playVideoIntent.setData(pin.getUri());
                        context.startActivity(playVideoIntent);
                        break;
                }
            }
        }

        void setTags(ArrayList<Tag> tags) {
            String tagList = "Tags: " + Tag.convertTagListToString(tags);
            this.pinTagsTextView.setText(tagList);
        }
    }
}