package com.brogrammer.imageclassificationlivefeed.UserInterface

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.brogrammer.imageclassificationlivefeed.R
import com.google.android.material.navigation.NavigationView

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        appBar = findViewById(R.id.appBar)
        mainDrawer = findViewById(R.id.mainDrawer)
        navigationView = findViewById(R.id.nav_view)

        //Changing the color of the navigation drawer item headings
        val menu = navigationView.menu
        val profileMenuItem = menu.findItem(R.id.nav_profile)

        profileMenuItem.title = SpannableString("Profile").apply {
            setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this@HomeActivity, R.color.black)),
                0,
                length,
                0
            )
        }
        ////////////////////////////////////

        // Changing the color of the divider in the NavigationView
        val color = ContextCompat.getColor(this, R.color.black)

        try {
            val menuViewField = NavigationView::class.java.getDeclaredField("mMenuView")
            menuViewField.isAccessible = true
            val menuView = menuViewField.get(navigationView)

            val dividerField = menuView.javaClass.getDeclaredField("mListDivider")
            dividerField.isAccessible = true
            val dividerDrawable = dividerField.get(menuView) as Drawable

            // Set the color of the divider
            dividerDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        } catch (e: Exception) {
            e.printStackTrace()
        }



        setUpViews()

        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment())
        }
    }
}