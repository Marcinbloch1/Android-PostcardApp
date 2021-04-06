package pl.polsl.lab6przykladkotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.webkit.JavascriptInterface
import android.widget.Toast
import java.util.*
import android.webkit.WebView
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1;
    private val REQUEST_IMAGE_CAPTURE = 2;
    private val presentsList: ArrayList<String> = ArrayList()
    private var currentPhotoPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val page = WebView(this)
        page.settings.javaScriptEnabled=true
        page.addJavascriptInterface(this, "activity")
        page.loadUrl("file:///android_asset/main.html")
        setContentView(page)
    }

    fun voiceRecognize() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pl-PL")
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Podaj prezent")
        try {
            startActivityForResult(intent, REQUEST_CODE)
        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    presentsList.add(result!![0])
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                generatePostcard()
            }
        }
    }

    @JavascriptInterface
    fun addPresent() {
        voiceRecognize()
    }

    @JavascriptInterface
    fun generatePostcard() {
        val intent = Intent(this, Postcard::class.java)
        intent.putStringArrayListExtra("Presents", presentsList)
        intent.putExtra("photoPath", currentPhotoPath)
        startActivity(intent)
    }

    @Throws(IOException::class)
    private fun createPhotoFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("RECEIVER${timeStamp}_", ".jpg", storageDir).apply { currentPhotoPath=absolutePath }
    }

    @SuppressLint("QueryPermissionsNeeded")
    @JavascriptInterface
    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createPhotoFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "pl.polsl.lab6przykladkotlin.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
}