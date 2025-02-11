package com.withpersona.demo.inlineInquirySample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.withpersona.demo.inlineInquirySample.databinding.ActivityMainBinding

/**
 * [MainActivity] hosts the navigation graph defined by [R.navigation.main].
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}