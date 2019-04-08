package com.weiggle.github.checkstyle

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       test.setOnClickListener{
           startActivity(Intent(this,InjectActivity::class.java))
       }

        second.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }

    }
}