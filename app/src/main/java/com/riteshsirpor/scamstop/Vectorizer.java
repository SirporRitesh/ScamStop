package com.riteshsirpor.scamstop;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Vectorizer {

    private static final String TAG = "Vectorizer";
    private static final String VOCAB_FILE = "tfidf_vocab.json";
    private static final String IDF_FILE = "tfidf_idf.json";

    private HashMap<String, Integer> vocab;
    private float[] idfValues;
    private int vocabSize;

    // Optional: enable punctuation removal
    private static final boolean CLEAN_PUNCTUATION = true;
    private static final Pattern PUNCT_PATTERN = Pattern.compile("[^a-zA-Z0-9\\s]");

    public Vectorizer(Context context) {
        try {
            String vocabJson = loadJsonFromAssets(context, VOCAB_FILE);
            String idfJson = loadJsonFromAssets(context, IDF_FILE);
            loadVocabFromJson(vocabJson);
            loadIDFFromJson(idfJson);

            // Sanity check
            if (idfValues.length != vocabSize) {
                Log.w(TAG, "Warning: IDF length (" + idfValues.length +
                        ") does not match vocab size (" + vocabSize + ")");
            }

        } catch (Exception e) {
            Log.e(TAG, "Initialization failed", e);
        }
    }


    private String loadJsonFromAssets(Context context, String fileName) throws Exception {
        InputStream is = context.getAssets().open(fileName);
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }


    private void loadVocabFromJson(String json) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        vocab = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            vocab.put(key, jsonObject.getInt(key));
        }

        vocabSize = vocab.size();
        Log.i(TAG, "Vocabulary loaded. Size: " + vocabSize);
    }


    private void loadIDFFromJson(String json) throws Exception {
        JSONArray jsonArray = new JSONArray(json);
        idfValues = new float[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            idfValues[i] = (float) jsonArray.getDouble(i);
        }

        Log.i(TAG, "IDF values loaded. Length: " + idfValues.length);
    }

    public float[] transform(String message) {
        float[] vector = new float[vocabSize];
        HashMap<String, Integer> termFreq = new HashMap<>();

        // Step 1: lowercase and optionally remove punctuation
        if (CLEAN_PUNCTUATION) {
            message = PUNCT_PATTERN.matcher(message).replaceAll("");
        }
        String[] tokens = message.toLowerCase().split("\\s+");

        // Step 2: count term frequencies
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            if (vocab.containsKey(token)) {
                if (termFreq.containsKey(token)) {
                    termFreq.put(token, termFreq.get(token) + 1);
                } else {
                    termFreq.put(token, 1);
                }
            } else {
                Log.d(TAG, "Missed token: " + token);
            }
        }

        // Step 3: apply TF-IDF = TF × IDF
        for (String token : termFreq.keySet()) {
            Integer index = vocab.get(token);
            Integer tf = termFreq.get(token);

            if (index != null && tf != null && index < idfValues.length) {
                vector[index] = tf * idfValues[index];
            }
        }

        return vector;
    }
}
