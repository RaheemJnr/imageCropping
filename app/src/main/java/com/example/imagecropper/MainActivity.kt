package com.example.imagecropper

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView

    //activity result callback
    private val pickImages =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageView = findViewById(R.id.cropperView)
            imageView.setImageBitmap(
                getRoundedCroppedBitmap(
                    decodeUriToBitmap(this, uri)!!
                )
            )
        }


    @Throws(IOException::class)
    fun decodeUriToBitmap(mContext: Context, sendUri: Uri?): Bitmap? {
        val getBitmap: Bitmap
        val imageStream: InputStream? = mContext.contentResolver.openInputStream(sendUri!!)
        getBitmap = BitmapFactory.decodeStream(imageStream)
        imageStream!!.close()
        return getBitmap
    }


    // onCreate activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // on click listener to open gallery
        val galleryButton = findViewById<Button>(R.id.gallery_button)
        galleryButton.setOnClickListener {
            pickImages.launch("image/*")
        }
    }

    // helper method to draw round circle on uploaded image
    private fun getRoundedCroppedBitmap(bitmap: Bitmap): Bitmap? {
        val widthLight = bitmap.width
        val heightLight = bitmap.height
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val shader = BitmapShader(
            bitmap,
            Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
        )

        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = shader
        paint.isFilterBitmap = true
        paint.isDither = true

        canvas.drawCircle(
            widthLight / 2 + 1F, heightLight / 2 + 1F, widthLight / 2 + 0.5F, paint
        )
        return output
    }


}
