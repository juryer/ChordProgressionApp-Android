package com.chordapp;

import java.util.ArrayList;
import java.util.List;

public class Song {
    private String id;
    private String title;
    private String key;
    private List<SongSection> sections;
    private long createdAt;
    private long updatedAt;

    public Song(String title) {
        this.id = java.util.UUID.randomUUID().toString();
        this.title = title;
        this.key = "C";
        this.sections = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public List<SongSection> getSections() { return sections; }
    public void setSections(List<SongSection> sections) { this.sections = new ArrayList<>(sections); }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    public void touch() { this.updatedAt = System.currentTimeMillis(); }

    public void addSection(SongSection section) {
        sections.add(section);
        touch();
    }

    public String getFormattedUpdatedAt() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.JAPAN);
        return sdf.format(new java.util.Date(updatedAt));
    }

    public String getSectionSummary() {
        if (sections == null || sections.isEmpty()) return "セクションなし";
        return sections.size() + " セクション";
    }
}
