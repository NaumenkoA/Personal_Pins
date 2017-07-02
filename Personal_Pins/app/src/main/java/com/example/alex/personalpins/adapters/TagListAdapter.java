package com.example.alex.personalpins.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.alex.personalpins.R;
import com.example.alex.personalpins.model.Tag;

import java.util.ArrayList;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {

        private ArrayList <Tag> tags;
        private TextView emptyTextView;

    public TagListAdapter(ArrayList <Tag> tags, TextView textView) {
        emptyTextView = textView;
        this.tags = tags;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tag_item_layout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.checkBox.setText(tags.get(position).getTagName());
        }

        @Override
        public int getItemCount() {
            emptyTextView.setVisibility(tags.size() > 0 ? View.GONE : View.VISIBLE);
            return (tags != null) ? tags.size():0;
        }

    public void refreshData(ArrayList<Tag> tagList) {
        tags = tagList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.tagCheckBox);
        }
    }
}
