package com.riteshsirpor.scamstop;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ModelUtils {

    private static final String TAG = "ModelUtils";

    public static MappedByteBuffer loadModelFile(Context context, String fileName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(fileName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        Log.i(TAG, "Model loaded from assets: " + fileName);
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}