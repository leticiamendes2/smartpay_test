package com.mendes.testcreditcard.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import com.mendes.testcreditcard.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.content_fragment) as NavHostFragment?
        val inflater: NavInflater? = navHostFragment?.navController?.navInflater
        val graph = inflater?.inflate(R.navigation.credit_card_nav_graph)

        if (graph != null) {
            navHostFragment.navController.graph = graph
        }
    }
}