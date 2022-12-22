package com.example.notekeeperapp

import com.example.notekeeperapp.files.CourseInfo
import com.example.notekeeperapp.files.NoteInfo
import java.util.*

object DataManager {
    val courses = HashMap<String, CourseInfo>() //maps string instances of CourseInfo and assigns them to courses property
    val notes = ArrayList<NoteInfo>() //an arraylist that holds NoteInfo references and assigns them to notes property

    /**this is an initializer block*/
    init {
        initializeCourses()//calls the initializeCourses function
        initializeNotes()
    }

    fun loadNotes(): List<NoteInfo> {
        simulateLoadDelay()
        return notes
    }

    fun loadNotes(vararg noteIds: Int): List<NoteInfo> {
        simulateLoadDelay()
        val noteList: List<NoteInfo>

        if(noteIds.isEmpty()) {
            noteList = notes
        }
        else {
            noteList = ArrayList<NoteInfo>(noteIds.size)
            for(noteId in noteIds)
                noteList.add(notes[noteId])
        }
        return noteList
    }

    fun loadNote(noteId: Int) = notes[noteId]

    fun isLastNoteId(noteId: Int) = noteId == notes.lastIndex

    private fun idOfNote(note: NoteInfo) = notes.indexOf(note)

    fun noteIdsAsIntArray(notes: List<NoteInfo>): IntArray {
        val noteIds = IntArray(notes.size)
        for(index in 0..notes.lastIndex)
            noteIds[index] = DataManager.idOfNote(notes[index])
        return noteIds
    }

    fun addNote(course: CourseInfo, noteTitle: String, noteText: String): Int {
        return addNote(NoteInfo(course, noteTitle, noteText))
    }

    private fun addNote(note: NoteInfo): Int{
        notes.add(note)
        return notes.lastIndex
    }

    fun findNote(course: CourseInfo, noteTitle: String, noteText: String) : NoteInfo? {
        for(note in notes)
            if (course == note.course && noteTitle == note.title && noteText == note.text) {
                return note
            }
        return null
    }

    private fun simulateLoadDelay() {
        Thread.sleep(1000)
    }

    private fun initializeCourses() {
        var course = CourseInfo("Android_Intents", "Android Programming with Intents") //creates a new course instance
        courses[course.courseId] = course //adds the new course(var course) to our courses collection

        course = CourseInfo("Android_Async", "Android Async Programming and Services")
        courses[course.courseId] = course

        course = CourseInfo(title = "Java Fundamentals: The Java Language", courseId = "Java_Lang")
        courses[course.courseId] = course

        course = CourseInfo(courseId = "Java_Core", title = "Java Fundamentals: The Core Platform")
        courses[course.courseId] = course
    }

    private fun initializeNotes() {
        var course = courses["Android_Intents"]!!
        var note = NoteInfo(course, "Dynamic Intent Resolution", "Intents allow components to be resolved at runtime")
        notes.add(note)
        note = NoteInfo(course, "Delegating Intents", "PendingIntents are powerful; they delegate much more than just a component invocation")
        notes.add(note)

        course = courses["Android_Async"]!!
        note = NoteInfo(course, "Service Default Threads", "By default, an Android Service will tie up the UI thread")
        notes.add(note)
        note = NoteInfo(course, "Long Running Operations", "Foreground Services can be tied to a notification icon")
        notes.add(note)

        course = courses["Java_Lang"]!!
        note = NoteInfo(course, "Parameters", "Leverage variable-length parameter lists")
        notes.add(note)
        note = NoteInfo(course, "Anonymous Classes", "They simplify implementing one-use types")
        notes.add(note)

        course = courses["Java_Core"]!!
        note = NoteInfo(course, "Compiler Options", "The -jar option is not compatible with the -cp option")
        notes.add(note)
        note = NoteInfo(course, "Serialization", "Remember to include SerialVersionUID to assure version compatibility")
        notes.add(note)
    }
}