package com.chordapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChordSelectActivity extends AppCompatActivity {

    // 選択済みコードリスト
    private List<String> selectedChords = new ArrayList<>();
    private LinearLayout selectedContainer;
    private TextView selectedLabel;

    // ダイアトニックコード（Cメジャー基準・修正版）
    private static final String[] DIATONIC = {"C", "Dm", "Em", "F", "G", "Am", "Bm(♭5)"};

    // カテゴリ別コード（♯なし）
    private static final String[] CAT_MAJOR     = {"C","D","E","F","G","A","B"};
    private static final String[] CAT_MAJOR_S   = {"C#","D#","F#","G#","A#"};
    private static final String[] CAT_MINOR     = {"Cm","Dm","Em","Fm","Gm","Am","Bm"};
    private static final String[] CAT_MINOR_S   = {"C#m","D#m","F#m","G#m","A#m"};
    private static final String[] CAT_7         = {"C7","D7","E7","F7","G7","A7","B7"};
    private static final String[] CAT_7_S       = {"C#7","D#7","F#7","G#7","A#7"};
    private static final String[] CAT_M7        = {"CM7","DM7","EM7","FM7","GM7","AM7","BM7"};
    private static final String[] CAT_M7_S      = {"C#M7","D#M7","F#M7","G#M7","A#M7"};
    private static final String[] CAT_m7        = {"Cm7","Dm7","Em7","Fm7","Gm7","Am7","Bm7"};
    private static final String[] CAT_m7_S      = {"C#m7","D#m7","F#m7","G#m7","A#m7"};
    private static final String[] CAT_SUS4      = {"Csus4","Dsus4","Esus4","Fsus4","Gsus4","Asus4","Bsus4"};
    private static final String[] CAT_SUS4_S    = {"C#sus4","D#sus4","F#sus4","G#sus4","A#sus4"};
    private static final String[] CAT_AUG       = {"Caug","Daug","Eaug","Faug","Gaug","Aaug","Baug"};
    private static final String[] CAT_DIM       = {"Cdim","Ddim","Edim","Fdim","Gdim","Adim","Bdim"};

    private LinearLayout categoryPanel;
    private LinearLayout diatonicPanel;
    private boolean showSharp = false;
    private int currentTab = 0; // 0=ダイア, 1=カテゴリ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chord_select);

        // 既存のコードを受け取る
        ArrayList<String> existing = getIntent().getStringArrayListExtra("existing_chords");
        if (existing != null) selectedChords = new ArrayList<>(existing);

        selectedContainer = findViewById(R.id.selected_container);
        selectedLabel = findViewById(R.id.selected_label);
        diatonicPanel = findViewById(R.id.diatonic_panel);
        categoryPanel = findViewById(R.id.category_panel);
        scrollDiatonic = findViewById(R.id.scroll_diatonic);
        scrollCategory = findViewById(R.id.scroll_category);

        // タブ切り替え（2タブのみ）
        findViewById(R.id.tab_diatonic).setOnClickListener(v -> showTab(0));
        findViewById(R.id.tab_category).setOnClickListener(v -> showTab(1));

        // ♯トグル
        Switch sharpToggle = findViewById(R.id.sharp_toggle);
        sharpToggle.setOnCheckedChangeListener((btn, checked) -> {
            showSharp = checked;
            buildCategoryPanel();
        });

        // ボタン
        findViewById(R.id.btn_undo).setOnClickListener(v -> {
            if (!selectedChords.isEmpty()) {
                selectedChords.remove(selectedChords.size() - 1);
                refreshSelected();
            }
        });
        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            selectedChords.clear();
            refreshSelected();
        });
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save).setOnClickListener(v -> {
            Intent result = new Intent();
            result.putStringArrayListExtra("chords", new ArrayList<>(selectedChords));
            setResult(RESULT_OK, result);
            finish();
        });

        buildDiatonicPanel();
        buildCategoryPanel();
        showTab(0);
        refreshSelected();
    }

    private View scrollCategory;
    private View scrollDiatonic;

    private void showTab(int tab) {
        currentTab = tab;
        scrollDiatonic.setVisibility(tab == 0 ? View.VISIBLE : View.GONE);
        scrollCategory.setVisibility(tab == 1 ? View.VISIBLE : View.GONE);
        diatonicPanel.setVisibility(tab == 0 ? View.VISIBLE : View.GONE);
        categoryPanel.setVisibility(tab == 1 ? View.VISIBLE : View.GONE);

        int activeColor = getResources().getColor(android.R.color.white, null);
        int inactiveColor = 0xFF888899;
        ((TextView)findViewById(R.id.tab_diatonic)).setTextColor(tab == 0 ? activeColor : inactiveColor);
        ((TextView)findViewById(R.id.tab_category)).setTextColor(tab == 1 ? activeColor : inactiveColor);
    }

    private void buildDiatonicPanel() {
        diatonicPanel.removeAllViews();
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(8, 8, 8, 8);
        for (String chord : DIATONIC) {
            row.addView(makeChordButton(chord, 0xFF7048e8));
        }
        diatonicPanel.addView(row);
    }

    private void buildCategoryPanel() {
        categoryPanel.removeAllViews();
        String[][]  normals = {CAT_MAJOR, CAT_MINOR, CAT_7, CAT_M7, CAT_m7, CAT_SUS4, CAT_AUG, CAT_DIM};
        String[][]  sharps  = {CAT_MAJOR_S, CAT_MINOR_S, CAT_7_S, CAT_M7_S, CAT_m7_S, CAT_SUS4_S, null, null};
        String[] catNames   = {"メジャー","マイナー","7th","M7","m7","sus4","aug","dim"};

        for (int c = 0; c < normals.length; c++) {
            String[] chords = (showSharp && sharps[c] != null) ? sharps[c] : normals[c];
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(4, 2, 4, 2);
            TextView label = new TextView(this);
            label.setText(catNames[c]);
            label.setTextColor(0xFFaaaacc);
            label.setTextSize(11);
            label.setWidth(80);
            label.setPadding(4, 0, 8, 0);
            label.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.addView(label);
            for (String chord : chords) {
                row.addView(makeChordButton(chord, 0xFF3c5c8c));
            }
            categoryPanel.addView(row);
        }
    }

    private Button makeChordButton(String chord, int color) {
        Button btn = new Button(this);
        btn.setText(chord);
        btn.setTextSize(12);
        btn.setPadding(12, 6, 12, 6);
        btn.setAllCaps(false); // 大文字変換を無効化
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(4, 2, 4, 2);
        btn.setLayoutParams(lp);
        btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        btn.setTextColor(0xFFffffff);
        btn.setOnClickListener(v -> addChord(chord));
        return btn;
    }

    private void addChord(String chord) {
        if (selectedChords.size() >= 8) {
            Toast.makeText(this, "最大8コードまでです", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedChords.add(chord);
        refreshSelected();
    }

    private void refreshSelected() {
        selectedContainer.removeAllViews();
        if (selectedChords.isEmpty()) {
            selectedLabel.setText("コードを選択してください");
            return;
        }
        selectedLabel.setText("選択済み: " + selectedChords.size() + " コード");
        for (String chord : selectedChords) {
            TextView chip = new TextView(this);
            chip.setText(chord);
            chip.setTextColor(0xFFffffff);
            chip.setTextSize(12);
            chip.setPadding(16, 6, 16, 6);
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF7048e8));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4, 2, 4, 2);
            chip.setLayoutParams(lp);
            selectedContainer.addView(chip);
        }
    }
}
