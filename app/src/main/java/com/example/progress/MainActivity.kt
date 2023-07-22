package com.example.progress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hexagonprogress.HexagonProgressView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        HexagonProgressView
    }
}