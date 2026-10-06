package com.shortdrama.ai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shortdrama.ai.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/** Single-activity host. Screens are fragments from feature modules wired in nav_graph.xml. */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
