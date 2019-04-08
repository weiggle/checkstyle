package com.weiggle.github.checkstyle

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.weiggle.github.apt_api.BindView
import com.weiggle.github.apt_api.ButterKnife

class SecondActivity : AppCompatActivity() {


    @BindView(R.id.button)
    lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        ButterKnife.inject(this)
        button.setOnClickListener {
            finish()
        }
    }
}
