package com.chordapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class SongEditorActivity extends AppCompatActivity {

    private SongRepository songRepo;
    private String editSongId = null;
    private String currentKey = "C";
    private List<SongSection> sections = new ArrayList<>();

    private EditText editSongTitle;
    private Spinner keySpinner;
    private LinearLayout sectionContainer;
    private TextView previewText;

    // コード選択からの結果を受け取るためのインデックス
    private int pendingSectionIndex = -1;

    private ActivityResultLauncher<Intent> chordSelectLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_editor);

        songRepo = SongRepository.getInstance(this);
        editSongTitle = findViewById(R.id.edit_song_title);
        keySpinner = findViewById(R.id.key_spinner);
        sectionContainer = findViewById(R.id.section_container);
        previewText = findViewById(R.id.preview_text);

        // キースピナー設定
        String[] keys = ChordTransposer.getKeys();
        ArrayAdapter<String> keyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, keys);
        keyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        keySpinner.setAdapter(keyAdapter);
        keySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newKey = keys[position];
                if (!newKey.equals(currentKey)) {
                    transposeSections(currentKey, newKey);
                    currentKey = newKey;
                    refreshSections();
                    updatePreview();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // コード選択結果を受け取る
        chordSelectLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null
                            && pendingSectionIndex >= 0
                            && pendingSectionIndex < sections.size()) {
                        ArrayList<String> chords = result.getData()
                                .getStringArrayListExtra("chords");
                        if (chords != null) {
                            sections.get(pendingSectionIndex).setChords(new ArrayList<>(chords));
                            refreshSections();
                            updatePreview();
                        }
                    }
                    pendingSectionIndex = -1;
                });

        // 編集モード
        editSongId = getIntent().getStringExtra("song_id");
        if (editSongId != null) {
            Song song = songRepo.findById(editSongId);
            if (song != null) {
                editSongTitle.setText(song.getTitle());
                currentKey = song.getKey();
                sections = new ArrayList<>(song.getSections());
                // キースピナーを設定
                for (int i = 0; i < keys.length; i++) {
                    if (keys[i].equals(currentKey)) {
                        keySpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        // ＋セクション追加
        findViewById(R.id.btn_add_section).setOnClickListener(v -> showAddSectionDialog());

        // 全クリア
        findViewById(R.id.btn_clear_all).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("全クリア")
                        .setMessage("全セクションを削除しますか？")
                        .setPositiveButton("クリア", (d, w) -> {
                            sections.clear();
                            refreshSections();
                            updatePreview();
                        })
                        .setNegativeButton("キャンセル", null)
                        .show());

        // 曲を保存
        findViewById(R.id.btn_save_song).setOnClickListener(v -> saveSong());

        // テキスト出力
        findViewById(R.id.btn_text_export).setOnClickListener(v -> showTextExport());

        // 閉じる
        findViewById(R.id.btn_close).setOnClickListener(v -> finish());

        editSongTitle.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { updatePreview(); }
            public void afterTextChanged(android.text.Editable s) {}
        });

        refreshSections();
        updatePreview();
    }

    private void transposeSections(String fromKey, String toKey) {
        int semitones = ChordTransposer.getSemitones(fromKey, toKey);
        for (SongSection section : sections) {
            List<String> transposed = new ArrayList<>();
            for (String chord : section.getChords()) {
                transposed.add(ChordTransposer.transpose(chord, semitones));
            }
            section.setChords(transposed);
        }
    }

    private void showAddSectionDialog() {
        String[] sectionNames = {"Aメロ","Bメロ","サビ","大サビ","イントロ","アウトロ","ブリッジ","間奏","その他"};
        new AlertDialog.Builder(this)
                .setTitle("セクションを追加")
                .setItems(sectionNames, (d, which) -> {
                    SongSection section = new SongSection(sectionNames[which]);
                    sections.add(section);
                    refreshSections();
                    updatePreview();
                    // コード選択画面へ
                    pendingSectionIndex = sections.size() - 1;
                    Intent intent = new Intent(this, ChordSelectActivity.class);
                    intent.putStringArrayListExtra("existing_chords", new ArrayList<>());
                    chordSelectLauncher.launch(intent);
                })
                .show();
    }

    private void refreshSections() {
        sectionContainer.removeAllViews();
        for (int i = 0; i < sections.size(); i++) {
            final int idx = i;
            SongSection section = sections.get(i);
            View card = getLayoutInflater().inflate(R.layout.item_editor_section, sectionContainer, false);

            ((TextView) card.findViewById(R.id.text_section_name)).setText("# " + section.getName());
            String chordsStr = section.getChordsAsString();
            ((TextView) card.findViewById(R.id.text_section_chords)).setText(
                    chordsStr.isEmpty() ? "（コードなし）" : chordsStr);

            // ▲
            card.findViewById(R.id.btn_up).setOnClickListener(v -> {
                if (idx > 0) {
                    SongSection tmp = sections.get(idx - 1);
                    sections.set(idx - 1, sections.get(idx));
                    sections.set(idx, tmp);
                    refreshSections();
                    updatePreview();
                }
            });
            // ▼
            card.findViewById(R.id.btn_down).setOnClickListener(v -> {
                if (idx < sections.size() - 1) {
                    SongSection tmp = sections.get(idx + 1);
                    sections.set(idx + 1, sections.get(idx));
                    sections.set(idx, tmp);
                    refreshSections();
                    updatePreview();
                }
            });
            // コード編集
            card.findViewById(R.id.btn_edit_chords).setOnClickListener(v -> {
                pendingSectionIndex = idx;
                Intent intent = new Intent(this, ChordSelectActivity.class);
                intent.putStringArrayListExtra("existing_chords",
                        new ArrayList<>(section.getChords()));
                chordSelectLauncher.launch(intent);
            });
            // 歌詞編集
            card.findViewById(R.id.btn_edit_lyrics).setOnClickListener(v -> {
                EditText lyricsEdit = new EditText(this);
                lyricsEdit.setText(section.getLyrics());
                lyricsEdit.setHint("歌詞を入力");
                lyricsEdit.setMinLines(3);
                new AlertDialog.Builder(this)
                        .setTitle("歌詞編集: " + section.getName())
                        .setView(lyricsEdit)
                        .setPositiveButton("保存", (d, w) -> {
                            section.setLyrics(lyricsEdit.getText().toString());
                            refreshSections();
                            updatePreview();
                        })
                        .setNegativeButton("キャンセル", null)
                        .show();
            });
            // 削除
            card.findViewById(R.id.btn_delete_section).setOnClickListener(v -> {
                sections.remove(idx);
                refreshSections();
                updatePreview();
            });

            sectionContainer.addView(card);
        }
    }

    private void updatePreview() {
        String title = editSongTitle.getText().toString().trim();
        if (title.isEmpty()) title = "（タイトルなし）";
        StringBuilder sb = new StringBuilder();
        String line = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
        sb.append(line).append("\n");
        sb.append("  ").append(title).append("  [ Key: ").append(currentKey).append(" ]\n");
        sb.append(line).append("\n");
        for (SongSection section : sections) {
            sb.append("# ").append(section.getName()).append("\n");
            sb.append("  ").append(section.getChordsAsString()).append("\n");
            if (!section.getLyrics().isEmpty()) {
                sb.append("  ♪ ").append(section.getLyrics()).append("\n");
            }
        }
        previewText.setText(sb.toString());
    }

    private void saveSong() {
        String title = editSongTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "曲名を入力してください", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editSongId != null) {
            Song song = songRepo.findById(editSongId);
            if (song != null) {
                song.setTitle(title);
                song.setKey(currentKey);
                song.setSections(sections);
                song.touch();
                songRepo.update(song);
                Toast.makeText(this, "「" + title + "」を更新しました", Toast.LENGTH_SHORT).show();
            }
        } else {
            Song song = new Song(title);
            song.setKey(currentKey);
            song.setSections(sections);
            songRepo.add(song);
            editSongId = song.getId();
            Toast.makeText(this, "「" + title + "」を保存しました", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTextExport() {
        String text = previewText.getText().toString();
        new AlertDialog.Builder(this)
                .setTitle("テキスト出力")
                .setMessage(text)
                .setPositiveButton("コピー", (d, w) -> {
                    android.content.ClipboardManager clipboard =
                            (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(
                            android.content.ClipData.newPlainText("song", text));
                    Toast.makeText(this, "クリップボードにコピーしました", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("閉じる", null)
                .show();
    }
}
