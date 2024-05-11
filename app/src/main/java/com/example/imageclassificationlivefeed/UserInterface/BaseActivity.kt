package com.example.imageclassificationlivefeed.UserInterface

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.imageclassificationlivefeed.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var mainDrawer: DrawerLayout
    lateinit var navigationView: NavigationView

    lateinit var appBar: MaterialToolbar
    private var selectedBottomNavItem: MenuItem? = null

    fun setUpViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        setUpDrawerLayout()
        setUpBottomNavigation()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    replaceFragment(DashboardFragment())
                    selectedBottomNavItem?.isChecked = false // Uncheck the previous item
                    bottomNavigationView.menu.findItem(R.id.dashboardFrag)?.isChecked = true // Check the corresponding bottom navigation item
//                    Toast.makeText(applicationContext, "Clicked Dashboard", Toast.LENGTH_SHORT).show()
                    mainDrawer.closeDrawer(GravityCompat.START)
                    // Handle Dashboard click action
                }
                R.id.nav_default_school -> {
//                    Toast.makeText(applicationContext, "Clicked Set Default School", Toast.LENGTH_SHORT).show()
                    // Handle Set Default School click action
                }
                R.id.nav_location -> {
//                    val intent = Intent(this, SecondActivity::class.java)
//                    startActivity(intent)
                    replaceFragment(SchoolLocationFragment())
//                    Toast.makeText(applicationContext, "Clicked School Location", Toast.LENGTH_SHORT).show()
                    mainDrawer.closeDrawer(GravityCompat.START)
                }
                R.id.nav_logout -> {
                    replaceFragment(EmployeeFragment())
                    selectedBottomNavItem?.isChecked = false
                    bottomNavigationView.menu.findItem(R.id.employeeFrag)?.isChecked = true
                    mainDrawer.closeDrawer(GravityCompat.START)
//                    Toast.makeText(applicationContext, "Clicked Logout", Toast.LENGTH_SHORT).show()
                    // Handle Logout click action
                }
            }
//            mainDrawer.closeDrawers(GravityCompat.START)
            true
        }
    }

    fun setUpBottomNavigation()
    {
//        bottomNavigationView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.dashboardFrag -> {
                    replaceFragment(DashboardFragment())
                }
                R.id.employeeFrag -> {
                    replaceFragment(EmployeeFragment())
                }
                else ->{

                }
            }
            selectedBottomNavItem = menuItem // Store the selected item
            true
        }
    }

    fun setUpDrawerLayout() {
        setSupportActionBar(appBar)
        supportActionBar?.title = ""
        actionBarDrawerToggle = ActionBarDrawerToggle(this, mainDrawer, R.string.app_name, R.string.app_name)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        // Remove the super call to prevent going back
        // super.onBackPressed()
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Optional: Add to backstack to handle fragment navigation
            .commit()
    }

}
