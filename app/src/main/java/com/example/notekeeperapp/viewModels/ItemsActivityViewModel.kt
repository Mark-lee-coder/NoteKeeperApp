package com.example.notekeeperapp.viewModels

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.example.notekeeperapp.DataManager
import com.example.notekeeperapp.R
import com.example.notekeeperapp.files.NoteInfo

class ItemsActivityViewModel : ViewModel() {
    private val maxRecentlyViewedNotes = 5
    val recentlyViewedNotes = ArrayList<NoteInfo>(maxRecentlyViewedNotes)
    var navDrawerDisplaySelectionName = "com.example.notekeeperapp.viewModels.ItemsActivityViewModel.navDrawerDisplaySelection"
    var recentlyViewedNoteIdsName = "com.example.notekeeperapp.viewModels.ItemsActivityViewModel.recentlyViewedNoteIds"
    var navDrawerDisplaySelection = R.id.nav_notes
    var isNewlyCreated = true

    fun addToRecentlyViewedNotes(note: NoteInfo) {
        // Check if selection is already in the list
        val existingIndex = recentlyViewedNotes.indexOf(note)
        if (existingIndex == -1) {
            // it isn't in the list...
            // Add new one to beginning of list and remove any beyond max we want to keep
            recentlyViewedNotes.add(0, note)
            for (index in recentlyViewedNotes.lastIndex downTo maxRecentlyViewedNotes)
                recentlyViewedNotes.removeAt(index)
        }
        else {
            // it is in the list...
            // Shift the ones above down the list and make it first member of the list
            for (index in (existingIndex - 1) downTo 0)
                recentlyViewedNotes[index + 1] = recentlyViewedNotes[index]
            recentlyViewedNotes[0] = note
        }
    }

    fun saveState(outState: Bundle) {
        outState.putInt(navDrawerDisplaySelectionName, navDrawerDisplaySelection)
        val noteIds = DataManager.noteIdsAsIntArray(recentlyViewedNotes)
        outState.putIntArray(recentlyViewedNoteIdsName, noteIds)
    }

    fun restoreState(savedInstanceState: Bundle) {
        navDrawerDisplaySelection = savedInstanceState.getInt(navDrawerDisplaySelectionName)
        val noteIds = savedInstanceState.getIntArray(recentlyViewedNoteIdsName)
        /**let is often used for executing a code block only with non-null values
        To perform actions on a non-null object, use the safe call operator ?. on it and call let with the actions in its lambda
        Another case for using let is introducing local variables with a limited scope for improving code readability*/
        val noteList = noteIds?.let {
            DataManager.loadNotes(*it)//*is a spread operator that expands the array
        }
        noteList?.let {
            recentlyViewedNotes.addAll(it)
        }
    }
}