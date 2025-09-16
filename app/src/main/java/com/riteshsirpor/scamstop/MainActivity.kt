package com.riteshsirpor.scamstop

import android.R
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val TAG = "ScamCheck"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // 🧠 Load vectorizer
            val vectorizer = Vectorizer(this)

            // 💬 Sample message to test
            val testMessage = "Congratulations! You’ve won a ₹5000 Amazon gift card!"

            // 🔣 Convert to TF-IDF
            val inputVector = vectorizer.transform(testMessage)

            // 🤖 Run model prediction
            val detector = ScamDetector(this)
            val prediction = detector.predict(inputVector)

            // 📣 Log prediction result
            Log.i(TAG, "Prediction: $prediction") // ~1.0 = scam, ~0.0 = safe

        } catch (e: Exception) {
            Log.e(TAG, "Error during test", e)
        }
    }
}