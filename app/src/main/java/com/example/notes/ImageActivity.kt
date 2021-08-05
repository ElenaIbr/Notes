package com.example.notes

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView

class ImageActivity : AppCompatActivity() {
    var imgOrientation = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val erlImage = findViewById<SubsamplingScaleImageView>(R.id.erlImage)
        val i = intent
        erlImage?.setImage(ImageSource.uri(Uri.parse(i.getStringExtra(IntentConstants.URI_KEY))))


        var turnBtn = findViewById<ImageButton>(R.id.turnBtn)
        turnBtn.setOnClickListener {
            if(imgOrientation>=270) {
                imgOrientation=0
                erlImage.orientation = imgOrientation
            }
            else{
                imgOrientation += 90
                erlImage.orientation = imgOrientation
            }
        }
    }
}