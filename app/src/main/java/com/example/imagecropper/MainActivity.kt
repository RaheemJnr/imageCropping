package com.example.imagecropper

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView

    //activity result callback
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { uri: ActivityResult ->
            if (uri.resultCode == Activity.RESULT_OK) {
                imageView = findViewById(R.id.cropperView)
                val selectedImage = uri.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                if (selectedImage != null) {
                    val cursor = contentResolver.query(
                        selectedImage.data!!,
                        filePathColumn, null, null, null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()

                        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                        val picturePath = cursor.getString(columnIndex)
                        imageView.setImageBitmap(
                            getRoundedCroppedBitmap(
                                BitmapFactory.decodeFile(
                                    picturePath
                                )
                            )
                        )
                        cursor.close()
                    }
                }

            }
        }

    // onCreate activity
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //intent
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // on click listener to open gallery
        val galleryButton = findViewById<Button>(R.id.gallery_button)
        galleryButton.setOnClickListener {
            startForResult.launch(intent)

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