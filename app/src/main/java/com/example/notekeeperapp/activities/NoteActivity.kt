package com.example.notekeeperapp.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import com.example.notekeeperapp.DataManager
import com.example.notekeeperapp.R
import com.example.notekeeperapp.databinding.ActivityNoteBinding
import com.example.notekeeperapp.files.CourseInfo
import com.example.notekeeperapp.files.NOTE_POSITION
import com.example.notekeeperapp.files.NoteInfo
import com.example.notekeeperapp.files.POSITION_NOT_SET
import kotlinx.android.synthetic.main.content_note.*

class NoteActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityNoteBinding
    private var notePosition = POSITION_NOT_SET

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val adapterCourses = ArrayAdapter(this, android.R.layout.simple_spinner_item, DataManager.courses.values.toList())
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourses.adapter = adapterCourses

        /*if the first part before the elvis operator(?:) else the second part after the ?:*/
        /**checks if the activity has been destroyed and recreated first otherwise performs the intent*/
        notePosition = savedInstanceState?.getInt(NOTE_POSITION, POSITION_NOT_SET)?: intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET)

        if (notePosition != POSITION_NOT_SET) {
            displayNote()
        }
        else {
            createNewNote()
        }
    }

    private fun createNewNote() {
        DataManager.notes.add(NoteInfo())//adds the new note to our notes collection
        notePosition = DataManager.notes.lastIndex//new note is added to the last index
    }

    /**the note index will note be reset in case the activity is killed for example by changing from portrait to landscape*/
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NOTE_POSITION, notePosition)
    }

    private fun displayNote() {
        val note = DataManager.notes[notePosition]//gets the note that corresponds with the note position selected
        textNoteTitle.setText(note.title)//display the title of the note selected within the views on the activity
        textNoteText.setText(note.text)//display the text of the note itself

        /**display the appropriate course for the note displayed*/
        val coursePosition = DataManager.courses.values.indexOf(note.course)//get the position of the appropriate course
        spinnerCourses.setSelection(coursePosition)//sets the selected course position to the spinner
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_next -> {
                moveNext()
                true
            }
            R.id.action_previous -> {
                movePrevious()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun moveNext() {
        ++notePosition//selects the next note in the list after the one we have selected
        displayNote()//called where there is a next note
        invalidateOptionsMenu()//called when the user gets to the end of the note
    }

    private fun movePrevious() {
        --notePosition
        displayNote()
        invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        /**if statement to check if we have reached the last index in the list*/
        if (DataManager.isLastNoteId(notePosition)) {
            val menuItem = menu?.findItem(R.id.action_next)
            /**if the menuItem is not null, change the next icon to block icon*/
            if (menuItem != null) {
                menuItem.icon = getDrawable(R.drawable.ic_baseline_block_24)//changes forward arrow to block arrow at the end of the list
                menuItem.isEnabled = false
            }
        }
        else if (notePosition.equals(DataManager.notes.first())) {
            val menuItem = menu?.findItem(R.id.action_previous)
            if (menuItem != null) {
                menuItem.icon = getDrawable(R.drawable.ic_baseline_block_24)
                menuItem.isEnabled = false
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        super.onPause()
        saveNote()
    }

    private fun saveNote() {
        /**saving content from the screen into the note within our DataManager*/
        val note = DataManager.notes[notePosition]//gets the position of our currently displayed note
        note.title = textNoteTitle.text.toString()//takes the string value of the title that is displayed and assign it to the notes title property
        note.text = textNoteText.text.toString()
        note.course = spinnerCourses.selectedItem as CourseInfo//gives a reference to the selected course and casts it to CourseInfo
    }
}