package com.chordapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ChordProgressionRepository repo;
    private SongRepository songRepo;
    private LinearLayout recentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repo = ChordProgressionRepository.getInstance(this);
        songRepo = SongRepository.getInstance(this);
        recentContainer = findViewById(R.id.recent_container);

        findViewById(R.id.btn_register).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        findViewById(R.id.btn_manage).setOnClickListener(v ->
                startActivity(new Intent(this, ManageActivity.class)));

        findViewById(R.id.btn_song_editor).setOnClickListener(v ->
                startActivity(new Intent(this, SongEditorActivity.class)));

        findViewById(R.id.btn_song_list).setOnClickListener(v ->
                startActivity(new Intent(this, SongListActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecentSongs();
    }

    private void refreshRecentSongs() {
        recentContainer.removeAllViews();
        List<Song> recents = songRepo.getRecentlyUpdated(3);

        if (recents.isEmpty()) {
            View empty = getLayoutInflater().inflate(R.layout.item_recent_card, recentContainer, false);
            ((TextView) empty.findViewById(R.id.text_recent_title)).setText("楽曲がありません");
            ((TextView) empty.findViewById(R.id.text_recent_chords)).setText("楽曲エディタから楽曲を作成してください");
            ((TextView) empty.findViewById(R.id.text_recent_bpm)).setText("");
            recentContainer.addView(empty);
            return;
        }

        for (Song song : recents) {
            View card = getLayoutInflater().inflate(R.layout.item_recent_card, recentContainer, false);
            ((TextView) card.findViewById(R.id.text_recent_title)).setText(song.getTitle());
            ((TextView) card.findViewById(R.id.text_recent_chords)).setText(
                    "Key: " + song.getKey() + "  " + song.getSectionSummary());
            ((TextView) card.findViewById(R.id.text_recent_bpm)).setText(
                    "最終更新: " + song.getFormattedUpdatedAt());

            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, SongEditorActivity.class);
                intent.putExtra("song_id", song.getId());
                startActivity(intent);
            });

            recentContainer.addView(card);
        }
    }
}
