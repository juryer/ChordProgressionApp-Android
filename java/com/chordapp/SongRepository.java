package com.chordapp;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SongRepository {
    private static SongRepository instance;
    private static final String FILE_NAME = "songs.json";
    private static final Gson GSON = new Gson();
    private List<Song> songs = new ArrayList<>();
    private Context context;

    private SongRepository(Context context) {
        this.context = context.getApplicationContext();
        List<Song> saved = load();
        if (saved != null && !saved.isEmpty()) {
            songs = saved;
        }
    }

    public static SongRepository getInstance(Context context) {
        if (instance == null) instance = new SongRepository(context);
        return instance;
    }

    private void save() {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(GSON.toJson(songs).getBytes("UTF-8"));
            fos.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private List<Song> load() {
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            fis.close();
            Type type = new TypeToken<List<Song>>(){}.getType();
            return GSON.fromJson(sb.toString(), type);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<Song> getAll() { return new ArrayList<>(songs); }

    public List<Song> getRecentlyUpdated(int limit) {
        return songs.stream()
                .sorted((a, b) -> Long.compare(b.getUpdatedAt(), a.getUpdatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void add(Song song) { songs.add(song); save(); }

    public void delete(String id) {
        songs.removeIf(s -> s.getId().equals(id));
        save();
    }

    public void update(Song updated) {
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getId().equals(updated.getId())) {
                songs.set(i, updated);
                save();
                return;
            }
        }
    }

    public Song findById(String id) {
        for (Song s : songs) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }
}
