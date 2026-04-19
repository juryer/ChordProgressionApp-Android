package com.chordapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChordProgressionAdapter extends RecyclerView.Adapter<ChordProgressionAdapter.ViewHolder> {

    private List<ChordProgression> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ChordProgression cp);
        void onItemLongClick(ChordProgression cp);
    }

    public ChordProgressionAdapter(List<ChordProgression> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chord_progression, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChordProgression cp = items.get(position);
        holder.titleText.setText(cp.getTitle());
        holder.chordsText.setText(cp.getChordsAsString());
        holder.memoText.setText(cp.getMemo());
        holder.ratingBar.setRating(cp.getRating());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(cp));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(cp);
            return true;
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateItems(List<ChordProgression> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView chordsText;
        TextView memoText;
        RatingBar ratingBar;

        ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.text_title);
            chordsText = itemView.findViewById(R.id.text_chords);
            memoText = itemView.findViewById(R.id.text_memo);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}
