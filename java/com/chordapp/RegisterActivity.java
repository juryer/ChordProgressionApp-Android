package com.chordapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import android.view.View;

public class RegisterActivity extends AppCompatActivity {

    private ChordProgressionRepository repo;
    private String editId = null;
    private List<String> selectedChords = new ArrayList<>();
    private LinearLayout chordChipContainer;
    private TextView chordEditBtn;

    private ActivityResultLauncher<Intent> chordSelectLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        repo = ChordProgressionRepository.getInstance(this);

        EditText editTitle = findViewById(R.id.edit_title);
        EditText editTempo = findViewById(R.id.edit_tempo);
        EditText editMemo = findViewById(R.id.edit_memo);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnCancel = findViewById(R.id.btn_cancel);
        TextView headerTitle = findViewById(R.id.header_title);
        chordChipContainer = findViewById(R.id.chord_chip_container);
        chordEditBtn = findViewById(R.id.chord_edit_btn);

        // コード選択画面からの結果を受け取る
        chordSelectLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ArrayList<String> chords = result.getData()
                                .getStringArrayListExtra("chords");
                        if (chords != null) {
                            selectedChords = new ArrayList<>(chords);
                            refreshChordChips();
                        }
                    }
                });

        // コード選択エリアをタップで選択画面へ
        View.OnClickListener openChordSelect = v -> {
            Intent intent = new Intent(this, ChordSelectActivity.class);
            intent.putStringArrayListExtra("existing_chords", new ArrayList<>(selectedChords));
            chordSelectLauncher.launch(intent);
        };
        chordChipContainer.setOnClickListener(openChordSelect);
        chordEditBtn.setOnClickListener(openChordSelect);

        // 編集モードの場合は既存データを読み込む
        editId = getIntent().getStringExtra("edit_id");
        if (editId != null) {
            ChordProgression cp = repo.findById(editId);
            if (cp != null) {
                headerTitle.setText("✏ コード進行編集");
                editTitle.setText(cp.getTitle());
                selectedChords = new ArrayList<>(cp.getChords());
                editTempo.setText(cp.getTempo());
                editMemo.setText(cp.getMemo());
                ratingBar.setRating(cp.getRating());
                btnSave.setText("更新する");
                refreshChordChips();
            }
        }

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String tempo = editTempo.getText().toString().trim();
            String memo = editMemo.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "タイトルを入力してください", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedChords.isEmpty()) {
                Toast.makeText(this, "コードを選択してください", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editId != null) {
                ChordProgression cp = repo.findById(editId);
                if (cp != null) {
                    cp.setTitle(title);
                    cp.setChords(selectedChords);
                    cp.setTempo(tempo.isEmpty() ? "120" : tempo);
                    cp.setMemo(memo);
                    cp.setRating((int) ratingBar.getRating());
                    cp.setLastUsed(System.currentTimeMillis());
                    repo.update(cp);
                    Toast.makeText(this, "「" + title + "」を更新しました！", Toast.LENGTH_SHORT).show();
                }
            } else {
                ChordProgression cp = new ChordProgression(
                        title, "C", tempo.isEmpty() ? "120" : tempo, selectedChords, memo);
                cp.setRating((int) ratingBar.getRating());
                repo.add(cp);
                Toast.makeText(this, "「" + title + "」を登録しました！", Toast.LENGTH_SHORT).show();
            }
            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private void refreshChordChips() {
        chordChipContainer.removeAllViews();
        if (selectedChords.isEmpty()) {
            TextView hint = new TextView(this);
            hint.setText("タップしてコードを選択...");
            hint.setTextColor(0xFF555577);
            hint.setTextSize(12);
            hint.setPadding(8, 8, 8, 8);
            chordChipContainer.addView(hint);
        } else {
            for (String chord : selectedChords) {
                TextView chip = new TextView(this);
                chip.setText(chord);
                chip.setTextColor(0xFFffffff);
                chip.setTextSize(12);
                chip.setPadding(14, 6, 14, 6);
                chip.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFF7048e8));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(4, 2, 4, 2);
                chip.setLayoutParams(lp);
                chordChipContainer.addView(chip);
            }
        }
        chordEditBtn.setText(selectedChords.isEmpty() ? "選択する" : "編集する");
    }
}
