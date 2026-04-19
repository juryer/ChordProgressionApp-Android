package com.chordapp;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String FILE_NAME = "progressions.json";
    private static final Gson GSON = new Gson();

    public static void save(Context context, List<ChordProgression> list) {
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(GSON.toJson(list).getBytes("UTF-8"));
            fos.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<ChordProgression> load(Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            fis.close();
            Type type = new TypeToken<List<ChordProgression>>(){}.getType();
            List<ChordProgression> list = GSON.fromJson(sb.toString(), type);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
