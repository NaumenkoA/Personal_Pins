package com.example.alex.personalpins.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.alex.personalpins.R;
import com.example.alex.personalpins.model.Board;
import com.example.alex.personalpins.ui.PinListActivity;

import java.io.IOException;
import java.util.ArrayList;

public class BoardListAdapter extends RecyclerView.Adapter<BoardListAdapter.ViewHolder> {

    private ArrayList<Board> boards;
    private Context context;
    private TextView emptyTextView;

    public BoardListAdapter(ArrayList <Board> boards, Context context, TextView emptyTextView) {
        this.boards = boards;
        this.context = context;
        this.emptyTextView = emptyTextView;
    }


    @Override
    public BoardListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_item_layout, parent, false);
        return new BoardListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Board board = boards.get(position);
        Bitmap bitmap = null;
        if (board.getCoverImageUriAsString().equals("")) {
            holder.boardImageView.setImageResource(R.drawable.default_image);
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.default_image);
               } else {
            Glide.with(context).load(board.getCoverImageUri()).into(holder.boardImageView);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), board.getCoverImageUri());
                } catch (IOException e) {
                e.printStackTrace();
            }
        }
        holder.boardTitleTextView.setText(board.getBoardTitle());
        Palette.Swatch vibrantSwatch = null;
        if (bitmap != null) {
            Palette palette = createPaletteSync(bitmap);
            vibrantSwatch = palette.getVibrantSwatch();
        }
        if (vibrantSwatch != null) {
            holder.boardTitleTextView.setTextColor(vibrantSwatch.getTitleTextColor());
            holder.boardTitleTextView.setBackgroundColor(vibrantSwatch.getRgb());
        }
    }

    @Override
    public int getItemCount() {
        emptyTextView.setVisibility(boards.size() > 0 ? View.GONE : View.VISIBLE);
        return (boards != null) ? boards.size():0;
    }

    public void refreshData(ArrayList<Board> boards) {
        this.boards = boards;
        notifyDataSetChanged();
    }

    public Palette createPaletteSync(Bitmap bitmap) {
        return Palette.from(bitmap).generate();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView boardImageView;
        public TextView boardTitleTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            boardImageView = (ImageView) itemView.findViewById(R.id.boardImageView);
            boardTitleTextView = (TextView) itemView.findViewById(R.id.boardTitleTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Board selectedBoard = boards.get(getAdapterPosition());
            Intent intent = new Intent(context, PinListActivity.class);
            intent.putExtra(PinListActivity.SELECTED_BOARD, selectedBoard);
            context.startActivity(intent);
        }
    }
}