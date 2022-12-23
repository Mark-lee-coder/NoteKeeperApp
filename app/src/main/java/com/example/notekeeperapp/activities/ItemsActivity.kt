package com.example.notekeeperapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notekeeperapp.DataManager
import com.example.notekeeperapp.R
import com.example.notekeeperapp.adapters.CourseRecyclerAdapter
import com.example.notekeeperapp.adapters.NoteRecyclerAdapter
import com.example.notekeeperapp.databinding.ActivityItemsBinding
import com.example.notekeeperapp.files.NoteInfo
import com.example.notekeeperapp.viewModels.ItemsActivityViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.app_bar_items.*
import kotlinx.android.synthetic.main.content_items.*

class ItemsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    NoteRecyclerAdapter.OnNoteSelectedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityItemsBinding

    /**the lazy keyword delays the creation of the instances until when required(onCreate() method runs)*/
    private val noteLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    private val noteRecyclerAdapter by lazy {
        val adapter = NoteRecyclerAdapter(this, DataManager.loadNotes())
        adapter.setOnSelectedListener(this)
        adapter
    }

    /**course_grid_span is found in the ui_constants.xml file in values folder*/
    private val courseLayoutManager by lazy {
        GridLayoutManager(this, resources.getInteger(R.integer.course_grid_span))
    }

    private val courseRecyclerAdapter by lazy {
        CourseRecyclerAdapter(this, DataManager.courses.values.toList())
    }

    private val recentlyViewedNoteRecyclerAdapter by lazy {
        val adapter = NoteRecyclerAdapter(this, viewModel.recentlyViewedNotes)
        adapter.setOnSelectedListener(this)
        adapter
    }

    /**the activity remains the same in case it is destroyed and recreated again(eg changing from portrait to landscape*/
    /**a reference to an instance of ItemsActivityViewModel*/
    private val viewModel by lazy {
        ViewModelProviders.of(this)[ItemsActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarItems.toolbar)

        binding.appBarItems.fab.setOnClickListener {
            startActivity(Intent(this, NoteActivity::class.java))
        }

        if (viewModel.isNewlyCreated && savedInstanceState != null) {
            viewModel.restoreState(savedInstanceState)
        }
        viewModel.isNewlyCreated = false

        handleDisplaySelection(viewModel.navDrawerDisplaySelection)

        /**enables the user to open and close the navigation drawer by tapping on the icon on the top left of the toolbar*/
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)//checks the current state of the navigation drawer
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_notes, R.id.nav_courses), drawerLayout)
    }

    /**storing the app in a durable state*/
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
       if (outState != null) {
           viewModel.saveState(outState)
       }
    }

    private fun displayNotes() {
        listItems.layoutManager = noteLayoutManager //associates listView with a layout
        listItems.adapter = noteRecyclerAdapter //associates listView with an adapter

        nav_view.menu.findItem(R.id.nav_notes).isChecked = true
    }

    private fun displayCourses() {
        listItems.layoutManager = courseLayoutManager
        listItems.adapter = courseRecyclerAdapter

        nav_view.menu.findItem(R.id.nav_courses).isChecked = true
    }

    private fun displayRecentlyViewedNotes() {
        listItems.layoutManager = noteLayoutManager
        listItems.adapter = recentlyViewedNoteRecyclerAdapter

        nav_view.menu.findItem(R.id.nav_recent_notes).isChecked = true
    }

    override fun onResume() {
        super.onResume()
        listItems.adapter?.notifyDataSetChanged()
    }

    /**closing the navigation drawer by pressing the back button*/
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            /**checks if any one of these conditions has been met*/
            R.id.nav_notes, R.id.nav_courses, R.id.nav_recent_notes -> {
                handleDisplaySelection(item.itemId)
                viewModel.navDrawerDisplaySelection = item.itemId//tracks the option that has been selected on the navigation drawer
            }
            R.id.nav_share -> {
                Snackbar.make(listItems, "Share where?", Snackbar.LENGTH_LONG).show()
            }
            R.id.nav_send -> {
                Snackbar.make(listItems, "Send where?", Snackbar.LENGTH_LONG).show()
            }
            /**nav_how_many_message_format can be found in strings.xml under values folder*/
            R.id.nav_how_many -> {
                val message = getString(R.string.nav_how_many_message_format, DataManager.notes.size, DataManager.courses.size)
                Snackbar.make(listItems, message, Snackbar.LENGTH_LONG).show()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun handleDisplaySelection(itemId: Int) {
        when (itemId) {
            R.id.nav_notes -> {
                displayNotes()
            }
            R.id.nav_courses -> {
                displayCourses()
            }
            R.id.nav_recent_notes -> {
                displayRecentlyViewedNotes()
            }
        }
    }

    override fun onNoteSelected(note: NoteInfo) {
        viewModel.addToRecentlyViewedNotes(note)
    }
}