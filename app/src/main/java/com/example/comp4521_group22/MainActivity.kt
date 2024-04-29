package com.example.comp4521_group22

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentList  = FragmentList.newInstance(0)
        val fragmentCalendar = FragmentCalendar()
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        thread {TodoDB.getDatabase(this) }.join()

        setCurrentFragment(fragmentList)

        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_list_view -> setCurrentFragment(fragmentList)
                R.id.menu_calendar_view -> setCurrentFragment(fragmentCalendar)
            }
            true
        }
    }

    fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.flFragment, fragment)
        commit()
    }

}



