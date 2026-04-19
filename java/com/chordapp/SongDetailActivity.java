package com.chordapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class SongDetailActivity extends AppCompatActivity {

    private SongRepository songRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        songRepo = SongRepository.getInstance(this);

        String songId = getIntent().getStringExtra("song_id");
        Song song = songRepo.findById(songId);

        if (song == null) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.text_detail_title)).setText(song.getTitle());
        ((TextView) findViewById(R.id.text_detail_key)).setText("Key: " + song.getKey());
        ((TextView) findViewById(R.id.text_detail_updated)).setText(
                "最終更新: " + song.getFormattedUpdatedAt());

        // セクション一覧
        LinearLayout sectionContainer = findViewById(R.id.section_container);
        List<SongSection> sections = song.getSections();

        if (sections == null || sections.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("セクションがありません");
            empty.setTextColor(0xFF555577);
            empty.setTextSize(13);
            empty.setPadding(8, 8, 8, 8);
            sectionContainer.addView(empty);
        } else {
            for (SongSection section : sections) {
                View card = LayoutInflater.from(this).inflate(
                        R.layout.item_section_card, sectionContainer, false);

                ((TextView) card.findViewById(R.id.text_section_name)).setText(
                        section.getName() + "  ×" + section.getRepeatCount());
                ((TextView) card.findViewById(R.id.text_section_chords)).setText(
                        section.getChordsAsString());

                if (!section.getLyrics().isEmpty()) {
                    TextView lyricsView = card.findViewById(R.id.text_section_lyrics);
                    lyricsView.setText(section.getLyrics());
                    lyricsView.setVisibility(View.VISIBLE);
                }

                sectionContainer.addView(card);
            }
        }

        // 戻るボタン
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 編集ボタン
        findViewById(R.id.btn_edit).setOnClickListener(v -> {
            Intent intent = new Intent(this, SongEditorActivity.class);
            intent.putExtra("song_id", songId);
            startActivity(intent);
            finish();
        });
    }
}
