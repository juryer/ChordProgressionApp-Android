package com.chordapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class SongListActivity extends AppCompatActivity {

    private SongRepository songRepo;
    private LinearLayout listContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        songRepo = SongRepository.getInstance(this);
        listContainer = findViewById(R.id.song_list_container);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_new_song).setOnClickListener(v ->
                startActivity(new Intent(this, SongEditorActivity.class)));

        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        listContainer.removeAllViews();
        List<Song> songs = songRepo.getAll();

        TextView countLabel = findViewById(R.id.text_song_count);
        countLabel.setText("全 " + songs.size() + " 曲");

        if (songs.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("楽曲がありません\n「新しい曲を作成」から作成してください");
            empty.setTextColor(0xFF555577);
            empty.setTextSize(14);
            empty.setGravity(android.view.Gravity.CENTER);
            empty.setPadding(0, 40, 0, 0);
            listContainer.addView(empty);
            return;
        }

        for (Song song : songs) {
            View card = LayoutInflater.from(this).inflate(R.layout.item_song_card, listContainer, false);

            ((TextView) card.findViewById(R.id.text_song_title)).setText(song.getTitle());
            ((TextView) card.findViewById(R.id.text_song_key)).setText("Key: " + song.getKey());
            ((TextView) card.findViewById(R.id.text_song_sections)).setText(song.getSectionSummary());
            ((TextView) card.findViewById(R.id.text_song_updated)).setText(
                    "最終更新: " + song.getFormattedUpdatedAt());

            // タップで詳細画面へ
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, SongDetailActivity.class);
                intent.putExtra("song_id", song.getId());
                startActivity(intent);
            });

            // 編集ボタン
            card.findViewById(R.id.btn_song_edit).setOnClickListener(v -> {
                Intent intent = new Intent(this, SongEditorActivity.class);
                intent.putExtra("song_id", song.getId());
                startActivity(intent);
            });

            // 削除ボタン
            card.findViewById(R.id.btn_song_delete).setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("削除")
                            .setMessage("「" + song.getTitle() + "」を削除しますか？")
                            .setPositiveButton("削除", (d, w) -> {
                                songRepo.delete(song.getId());
                                refreshList();
                            })
                            .setNegativeButton("キャンセル", null)
                            .show());

            listContainer.addView(card);
        }
    }
}
