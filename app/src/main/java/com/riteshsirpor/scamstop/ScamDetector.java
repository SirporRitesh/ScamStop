package com.riteshsirpor.scamstop;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ScamDetector {

    private static final String TAG = "ScamDetector";
    private static final String MODEL_NAME = "sms_model.tflite";
    private static final int FLOAT_SIZE = 4; // bytes

    private Interpreter interpreter;

    public ScamDetector(Context context) {
        try {
            interpreter = new Interpreter(ModelUtils.loadModelFile(context, MODEL_NAME));
        } catch (IOException e) {
            Log.e(TAG, "Failed to initialize interpreter", e);
            interpreter = null;
        }
    }

    public float predict(float[] inputData) {
        if (interpreter == null) {
            Log.w(TAG, "Interpreter is not initialized");
            return -1f; // convention: return negative if failed to infer
        }

        float[][] output = new float[1][1];
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(inputData.length * FLOAT_SIZE);
        inputBuffer.order(ByteOrder.nativeOrder());

        for (float val : inputData) {
            inputBuffer.putFloat(val);
        }

        try {
            interpreter.run(inputBuffer, output);
            return output[0][0];
        } catch (Exception e) {
            Log.e(TAG, "Prediction failed", e);
            return -1f;
        }
    }
}