package com.qiniu.qplayer2.ui.page.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.qiniu.qplayer2.R
import com.qiniu.qplayer2.ui.page.longvideo.LongVideoActivity
import com.qiniu.qplayer2.ui.page.simplelongvideo.SimpleLongVideoActivity
import com.qiniu.qplayer2.ui.page.setting.SettingActivity
import com.qiniu.qplayer2.ui.page.shortvideo.ShortVideoActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.simple_long_video_player_BTN).setOnClickListener {
            val intent = Intent(this, SimpleLongVideoActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.long_video_player_BTN).setOnClickListener {
            val intent = Intent(this, LongVideoActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.setting_BTN).setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.short_video_player_BTN).setOnClickListener {
            val intent = Intent(this, ShortVideoActivity::class.java)
            startActivity(intent)
        }

        // Example of a call to a native method
    }
}