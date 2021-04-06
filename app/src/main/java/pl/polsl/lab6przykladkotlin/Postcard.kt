package pl.polsl.lab6przykladkotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.webkit.WebView
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Postcard : AppCompatActivity() {

    private lateinit var photoPath: String;

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val page = WebView(this)
        page.settings.javaScriptEnabled=true
        page.addJavascriptInterface(this, "activity")
        page.loadUrl("file:///android_asset/postcard.html")
        setContentView(page)

        val intent = intent;
        photoPath = intent.getStringExtra("photoPath").toString()
    }

    fun getPresents(): String?{
        val presents = intent.getStringArrayListExtra("Presents")
        var allPresents = ""

        for (present in presents!!) {
            allPresents = allPresents + " - " + present + "<br>"
        }
        return allPresents
    }

    @JavascriptInterface
    fun getPresentsList(): String? {
        return this.getPresents()
    }

    @JavascriptInterface
    fun getPhotoPath() :String {
        return photoPath
    }
}