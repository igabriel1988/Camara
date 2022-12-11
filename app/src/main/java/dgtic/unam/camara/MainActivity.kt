package dgtic.unam.camara

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity()
{
    private lateinit var imageView:ImageView
    private lateinit var button:ImageButton
    private lateinit var currentPthotoPath: String


    override fun onCreate(savedInstanceState: Bundle?)
            {



                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)

                imageView = findViewById( R.id.imageView )
                button = findViewById( R.id.imageButton )

                if( ContextCompat.checkSelfPermission(
                        this@MainActivity,
                         android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )  != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                ){
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf( android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA ),
                        1000
                    )
                }
                dispatchTakePictureIntent()
            }

        @Throws( IOException::class )
        private fun createImageFile() : File{
            val timeStamp : String = SimpleDateFormat( "yyyyMMdd HHmmss" ).format( Date() )
            val storageDir : File? = getExternalFilesDir( Environment.DIRECTORY_PICTURES )
            return File.createTempFile(
                "JPEG ${timeStamp}",
                ".jpg",
                storageDir
            ).apply {
                currentPthotoPath = absolutePath
            }
        }

    private fun dispatchTakePictureIntent()
        {
                Intent( MediaStore.ACTION_IMAGE_CAPTURE ).also { takePictureIntent ->
                    takePictureIntent.resolveActivity( packageManager ).also {
                        val photoFile : File? = try{
                            createImageFile()
                        }catch (ex:IOException){
                            null
                        }


                       photoFile?.also{
                            var photoURI : Uri = FileProvider.getUriForFile(
                                this,
                                "dgtic.unam.camara.FileProvider",
                                it
                            )

                            takePictureIntent.putExtra( MediaStore.EXTRA_OUTPUT, photoURI )
                            val data = registerForActivityResult( ActivityResultContracts.TakePicture() )
                                    {
                                        imageView.setImageURI( null )
                                        imageView.setImageURI( photoURI )
                                        println( photoURI.encodedPath )
                                    }
                            button.setOnClickListener(){
                                data.launch( photoURI )
                            }
                        }

                    }
                }
    }

}