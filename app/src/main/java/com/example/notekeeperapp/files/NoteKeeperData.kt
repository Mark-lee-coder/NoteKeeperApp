package com.example.notekeeperapp.files

//class declaration with a method that has a return value
data class CourseInfo (val courseId: String, val title: String) {
    override fun toString(): String {
        return title
    }
}

//class declaration without a method
data class NoteInfo(var course: CourseInfo? = null, var title: String? = null, var text: String? = null)