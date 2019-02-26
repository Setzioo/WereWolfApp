package fr.isen.cata.werewolfapp

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_user_settings.*


class UserSettingsActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)

        val permissionNotGranted = getAllPermissionNotGranted()

        ActivityCompat.requestPermissions(this, permissionNotGranted, MY_PERMISSIONS_REQUEST)

        settingsView.setOnClickListener {
            chooseGalleryOrCamera()
        }
    }

    private fun getAllPermissionNotGranted(): Array<String> {

        return arrayOf(Manifest.permission.CAMERA)
    }

    private fun chooseGalleryOrCamera() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Quelle action souhaitez vous faire ?")
        val pictureDialogItems = arrayOf("Choisir une de vos photos", "Utiliser la caméra")
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choiceIsGallery()
                1 -> choiceIsCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choiceIsGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, 1)
    }

    private fun choiceIsCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 2)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1)
        {
            if (data != null)
            {
                val contentURI = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                avatarView.setImageBitmap(bitmap)
            }

        }
        else if (requestCode == 2)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            avatarView!!.setImageBitmap(thumbnail)
        }

    }
}
