package shinei.com.dougaku.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
