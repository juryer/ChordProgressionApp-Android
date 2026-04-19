package com.chordapp;

import android.content.Context;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ChordProgressionRepository {
    private static ChordProgressionRepository instance;
    private List<ChordProgression> progressions = new ArrayList<>();
    private Context context;

    private ChordProgressionRepository(Context context) {
        this.context = context.getApplicationContext();
        List<ChordProgression> saved = DataManager.load(this.context);
        if (saved != null && !saved.isEmpty()) {
            progressions = saved;
        } else {
            loadSampleData();
        }
    }

    public static ChordProgressionRepository getInstance(Context context) {
        if (instance == null) instance = new ChordProgressionRepository(context);
        return instance;
    }

    private void loadSampleData() {
        List<String> c1 = new ArrayList<>();
        c1.add("C"); c1.add("Am"); c1.add("F"); c1.add("G");
        progressions.add(new ChordProgression("王道ポップ進行", "C", "120", c1, "定番のI-VI-IV-V進行"));

        List<String> c2 = new ArrayList<>();
        c2.add("Am"); c2.add("F"); c2.add("G"); c2.add("C");
        progressions.add(new ChordProgression("小室進行", "C", "130", c2, "ポップスで多用される進行"));

        List<String> c3 = new ArrayList<>();
        c3.add("C"); c3.add("G"); c3.add("Am"); c3.add("F");
        progressions.add(new ChordProgression("カノン進行", "C", "90", c3, "パッヘルベルのカノンをベースにした進行"));
    }

    public void save() { DataManager.save(context, progressions); }

    public List<ChordProgression> getAll() { return new ArrayList<>(progressions); }

    public List<ChordProgression> getRecentlyUsed(int limit) {
        return progressions.stream()
                .sorted((a, b) -> Long.compare(b.getLastUsed(), a.getLastUsed()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void add(ChordProgression cp) { progressions.add(cp); save(); }

    public void delete(String id) {
        progressions.removeIf(p -> p.getId().equals(id));
        save();
    }

    public void update(ChordProgression updated) {
        for (int i = 0; i < progressions.size(); i++) {
            if (progressions.get(i).getId().equals(updated.getId())) {
                progressions.set(i, updated);
                save();
                return;
            }
        }
    }

    public void moveUp(String id) {
        for (int i = 1; i < progressions.size(); i++) {
            if (progressions.get(i).getId().equals(id)) {
                ChordProgression tmp = progressions.get(i - 1);
                progressions.set(i - 1, progressions.get(i));
                progressions.set(i, tmp);
                save();
                return;
            }
        }
    }

    public void moveDown(String id) {
        for (int i = 0; i < progressions.size() - 1; i++) {
            if (progressions.get(i).getId().equals(id)) {
                ChordProgression tmp = progressions.get(i + 1);
                progressions.set(i + 1, progressions.get(i));
                progressions.set(i, tmp);
                save();
                return;
            }
        }
    }

    public ChordProgression findById(String id) {
        for (ChordProgression cp : progressions) {
            if (cp.getId().equals(id)) return cp;
        }
        return null;
    }
}
