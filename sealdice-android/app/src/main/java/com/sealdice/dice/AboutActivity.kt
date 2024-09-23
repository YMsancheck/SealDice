package com.sealdice.dice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_about)
        findViewById<TextView>(R.id.about_app_version).text = "App Version: " + BuildConfig.VERSION_NAME
        val buttonReport = findViewById<Button>(R.id.button_report)
        var clickTitleTimes = 0
        buttonReport.setOnClickListener {
            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(
                this, R.style.Theme_Mshell_DialogOverlay
            )
            alertDialogBuilder.setTitle("提示")
            alertDialogBuilder.setMessage("将跳转至网页，如果打不开请尝试更换网络或使用魔法")
            alertDialogBuilder.setPositiveButton("确定") { _: android.content.DialogInterface, _: Int ->
                val intent = Intent("android.intent.action.VIEW")
                intent.data = Uri.parse("https://github.com/sealdice/sealdice-android/issues/new/choose")
                startActivity(intent)
            }
        }
        val buttonRepo = findViewById<Button>(R.id.button_repo)
        buttonRepo.setOnClickListener {
            val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(
                this, R.style.Theme_Mshell_DialogOverlay
            )
            alertDialogBuilder.setTitle("提示")
            alertDialogBuilder.setMessage("将跳转至网页，如果打不开请尝试更换网络或使用魔法")
            alertDialogBuilder.setPositiveButton("确定") { _: android.content.DialogInterface, _: Int ->
                val intent = Intent("android.intent.action.VIEW")
                intent.data = Uri.parse("https://github.com/sealdice/sealdice-android")
                startActivity(intent)
            }
        }
        findViewById<ImageView>(R.id.about_app_icon).setOnClickListener {
            it.animate().rotationBy(360f).setDuration(1000).start()
        }
        findViewById<TextView>(R.id.about_app_name).setOnClickListener {
            if (clickTitleTimes >= 2) {
                val intent = Intent(this, DebugActivity::class.java)
                startActivity(intent)
            } else {
                clickTitleTimes++
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}