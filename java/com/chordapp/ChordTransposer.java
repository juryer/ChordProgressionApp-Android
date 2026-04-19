package com.chordapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChordTransposer {

    private static final String[] NOTES = {
        "C","C#","D","D#","E","F","F#","G","G#","A","A#","B"
    };
    private static final String[] NOTES_FLAT = {
        "C","Db","D","Eb","E","F","Gb","G","Ab","A","Bb","B"
    };
    private static final String[] KEYS = {
        "C","C#","D","D#","E","F","F#","G","G#","A","A#","B",
        "Cm","C#m","Dm","D#m","Em","Fm","F#m","Gm","G#m","Am","A#m","Bm"
    };

    public static String[] getKeys() { return KEYS; }

    /** コード1つを移調する */
    public static String transpose(String chord, int semitones) {
        if (chord == null || chord.isEmpty()) return chord;
        if (semitones == 0) return chord;

        // オンコード対応（C/E形式）
        if (chord.contains("/")) {
            String[] parts = chord.split("/", 2);
            return transpose(parts[0], semitones) + "/" + transpose(parts[1], semitones);
        }

        String root = extractRoot(chord);
        if (root == null) return chord;

        String suffix = chord.substring(root.length());
        int idx = getNoteIndex(root);
        if (idx < 0) return chord;

        int newIdx = ((idx + semitones) % 12 + 12) % 12;
        return NOTES[newIdx] + suffix;
    }

    /** コードリストをCキー基準から指定キーへ移調 */
    public static List<String> transposeList(List<String> chords, String targetKey) {
        int semitones = getSemitones("C", targetKey);
        List<String> result = new ArrayList<>();
        for (String chord : chords) {
            result.add(transpose(chord, semitones));
        }
        return result;
    }

    /** 2つのキー間の半音差を計算 */
    public static int getSemitones(String fromKey, String toKey) {
        String fromRoot = fromKey.replace("m","").replace("M","");
        String toRoot = toKey.replace("m","").replace("M","");
        int from = getNoteIndex(fromRoot);
        int to = getNoteIndex(toRoot);
        if (from < 0 || to < 0) return 0;
        return ((to - from) % 12 + 12) % 12;
    }

    private static String extractRoot(String chord) {
        if (chord.length() >= 2 && (chord.charAt(1) == '#' || chord.charAt(1) == 'b')) {
            return chord.substring(0, 2);
        }
        return chord.substring(0, 1);
    }

    private static int getNoteIndex(String note) {
        for (int i = 0; i < NOTES.length; i++) {
            if (NOTES[i].equals(note)) return i;
        }
        for (int i = 0; i < NOTES_FLAT.length; i++) {
            if (NOTES_FLAT[i].equals(note)) return i;
        }
        return -1;
    }
}
