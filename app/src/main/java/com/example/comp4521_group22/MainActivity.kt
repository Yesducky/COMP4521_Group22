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

        //init database
        thread {
            TodoDB.getDatabase(this)
            HabitDB.getDatabase(this)
        }.join()

        //set the main fragment
        setCurrentFragment(fragmentList)

        //bottom navigation bar
        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_list_view -> setCurrentFragment(fragmentList)
                R.id.menu_calendar_view -> setCurrentFragment(fragmentCalendar)
            }
            true
        }
    }

    //same as lecture notes
    fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.flFragment, fragment)
        commit()
    }

}



