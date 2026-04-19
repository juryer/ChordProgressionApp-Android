package com.chordapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ManageActivity extends AppCompatActivity {

    private ChordProgressionRepository repo;
    private LinearLayout listContainer;
    private EditText searchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        repo = ChordProgressionRepository.getInstance(this);
        listContainer = findViewById(R.id.list_container);
        searchField = findViewById(R.id.search_field);

        searchField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { refreshList(); }
            public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        listContainer.removeAllViews();
        String query = searchField.getText().toString().toLowerCase().trim();
        List<ChordProgression> list = repo.getAll();

        for (int i = 0; i < list.size(); i++) {
            ChordProgression cp = list.get(i);
            if (!query.isEmpty()) {
                boolean match = cp.getTitle().toLowerCase().contains(query)
                        || cp.getChordsAsString().toLowerCase().contains(query);
                if (!match) continue;
            }

            final int index = i;
            View card = LayoutInflater.from(this).inflate(R.layout.item_manage_card, listContainer, false);

            ((TextView) card.findViewById(R.id.text_title)).setText(cp.getTitle());
            ((TextView) card.findViewById(R.id.text_chords)).setText(cp.getChordsAsString());
            ((TextView) card.findViewById(R.id.text_meta)).setText(
                    "BPM: " + cp.getTempo() + "  " + cp.getStars());
            TextView memoView = card.findViewById(R.id.text_memo);
            if (!cp.getMemo().isEmpty()) memoView.setText(cp.getMemo());
            else memoView.setVisibility(View.GONE);

            // 上へボタン
            card.findViewById(R.id.btn_up).setOnClickListener(v -> {
                repo.moveUp(cp.getId());
                refreshList();
            });

            // 下へボタン
            card.findViewById(R.id.btn_down).setOnClickListener(v -> {
                repo.moveDown(cp.getId());
                refreshList();
            });

            // 編集ボタン
            card.findViewById(R.id.btn_edit).setOnClickListener(v -> {
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("edit_id", cp.getId());
                startActivity(intent);
            });

            // 削除ボタン
            card.findViewById(R.id.btn_delete).setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("削除")
                            .setMessage("「" + cp.getTitle() + "」を削除しますか？")
                            .setPositiveButton("削除", (d, w) -> {
                                repo.delete(cp.getId());
                                refreshList();
                            })
                            .setNegativeButton("キャンセル", null)
                            .show());

            listContainer.addView(card);
        }
    }
}
