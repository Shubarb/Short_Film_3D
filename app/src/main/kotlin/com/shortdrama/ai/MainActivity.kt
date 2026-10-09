package com.shortdrama.ai

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.shortdrama.ai.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/** Single-activity host. Screens are fragments wired in nav_graph.xml, starting at the Main screen. */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Draw behind the system bars: the Home banner runs under the status bar and the
        // bottom navigation floats above the gesture bar, as in the design. Screens pad themselves.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
