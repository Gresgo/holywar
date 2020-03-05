package com.example.wtfwar.settings

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.example.wtfwar.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class AvatarSettings : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private lateinit var user: FirebaseUser
    private lateinit var change: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_avatar)

        user = FirebaseAuth.getInstance().currentUser!!

        val choose = findViewById<Button>(R.id.choose)
        choose.setOnClickListener {
            chooseImg()
        }

        choose.setBackgroundResource(R.drawable.button_back)

        change = findViewById(R.id.change)
        change.setOnClickListener {
            uploadImg()
        }
        change.isClickable = false
        change.setBackgroundResource(R.drawable.transparent)
    }

    private fun chooseImg(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImg(){

        if (filePath!=null){
            change.isClickable = false
            val storageRef = FirebaseStorage.getInstance().reference.child("avatars/"+user.uid)
            storageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    change.isClickable = true
                    Toast.makeText(this@AvatarSettings, "Uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    change.isClickable = true
                    Toast.makeText(this@AvatarSettings, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(this@AvatarSettings, "Choose image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data!=null && data.data!=null){

            filePath = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                Toast.makeText(this@AvatarSettings, bitmap.width.toString(), Toast.LENGTH_SHORT).show()
                if (bitmap.width <= 2500 && bitmap.height <= 2500
                    && (bitmap.height/bitmap.width <= 4) && (bitmap.width/bitmap.height <= 4)){
                    val example = findViewById<ImageView>(R.id.example)
                    example.setImageBitmap(bitmap)
                    change.isClickable = true
                    change.setBackgroundResource(R.drawable.button_back)
                }else{
                    Toast.makeText(this@AvatarSettings, "Image is too big", Toast.LENGTH_SHORT).show()
                }

//                val ob = BitmapDrawable(resources, bitmap)
//                example.background = ob
//                uploadImg()
            }catch (ex: IOException){
                Toast.makeText(this@AvatarSettings, "error", Toast.LENGTH_SHORT).show()
            }
        }
    }

}