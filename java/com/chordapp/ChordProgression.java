package com.chordapp;

import java.util.ArrayList;
import java.util.List;

public class ChordProgression {
    private String id;
    private String title;
    private String key;
    private String tempo;
    private List<String> chords;
    private String memo;
    private int rating;
    private long lastUsed;

    public ChordProgression(String title, String key, String tempo, List<String> chords, String memo) {
        this.id = java.util.UUID.randomUUID().toString();
        this.title = title;
        this.key = key;
        this.tempo = tempo;
        this.chords = new ArrayList<>(chords);
        this.memo = memo != null ? memo : "";
        this.rating = 3;
        this.lastUsed = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getTempo() { return tempo; }
    public void setTempo(String tempo) { this.tempo = tempo; }
    public List<String> getChords() { return chords; }
    public void setChords(List<String> chords) { this.chords = new ArrayList<>(chords); }
    public String getMemo() { return memo != null ? memo : ""; }
    public void setMemo(String memo) { this.memo = memo; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = Math.max(1, Math.min(5, rating)); }
    public long getLastUsed() { return lastUsed; }
    public void setLastUsed(long lastUsed) { this.lastUsed = lastUsed; }

    public String getChordsAsString() {
        if (chords == null) return "";
        return android.text.TextUtils.join(" → ", chords);
    }

    public String getStars() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) sb.append(i <= rating ? "★" : "☆");
        return sb.toString();
    }
}
