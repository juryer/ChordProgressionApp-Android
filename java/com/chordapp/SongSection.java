package com.chordapp;

import java.util.ArrayList;
import java.util.List;

public class SongSection {
    private String name;
    private List<String> chords;
    private String lyrics;
    private int repeatCount;

    public SongSection(String name) {
        this.name = name;
        this.chords = new ArrayList<>();
        this.lyrics = "";
        this.repeatCount = 1;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getChords() { return chords; }
    public void setChords(List<String> chords) { this.chords = new ArrayList<>(chords); }
    public String getLyrics() { return lyrics; }
    public void setLyrics(String lyrics) { this.lyrics = lyrics != null ? lyrics : ""; }
    public int getRepeatCount() { return repeatCount; }
    public void setRepeatCount(int repeatCount) { this.repeatCount = Math.max(1, repeatCount); }

    public String getChordsAsString() {
        if (chords == null || chords.isEmpty()) return "";
        return android.text.TextUtils.join(" → ", chords);
    }
}
