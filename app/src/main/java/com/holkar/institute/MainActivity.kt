package com.holkar.institute

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ek simple welcome text layout banana
        val textView = TextView(this).apply {
            text = "Welcome to Holkar Institute\n\n- SSC\n- Railway\n- State PSC\n- Force\n- 10th & 12th Mathematics"
            textSize = 20f
            setPadding(50, 50, 50, 50)
        }
        
        setContentView(textView)
    }
}
