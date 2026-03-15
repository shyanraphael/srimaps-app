package com.example.srimaps_app

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        
        // Setup the shared element transition
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(R.id.logo_container)
            duration = 1000L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(R.id.logo_container)
            duration = 1000L
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val blobs = listOf(
            findViewById<View>(R.id.main_blob1),
            findViewById<View>(R.id.main_blob2),
            findViewById<View>(R.id.main_blob3),
            findViewById<View>(R.id.main_blob4),
            findViewById<View>(R.id.main_blob5),
            findViewById<View>(R.id.main_blob6),
            findViewById<View>(R.id.main_blob7),
            findViewById<View>(R.id.main_blob8),
            findViewById<View>(R.id.main_blob9)
        )

        blobs.forEachIndexed { index, blob ->
            blob?.let { startFloatingAnimation(it, index) }
        }

        findViewById<Button>(R.id.btn_get_started).setOnClickListener {
            val intent = Intent(this, LanguageSelectionActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                findViewById(R.id.logo_container),
                "logo_transition"
            )
            startActivity(intent, options.toBundle())
        }
    }

    private fun startFloatingAnimation(view: View, index: Int) {
        val xRange = if (index % 2 == 0) 15f else -15f
        val yRange = if (index % 3 == 0) 25f else -25f
        
        val pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, xRange)
        val pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, yRange)
        
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY).apply {
            duration = 2500L + (index * 150)
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }
}
