package com.mike.myclass

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar



open class User(
    val id: String = MyDatabase.generateIndexNumber(),
    val name: String = "",
    val email: String = "",

    )

open class Admin(

    val password: String = ""

): User()

data class Timetable(
    val id: String = MyDatabase.generateTimetableID(),
    val startTime: String = "Calendat",
    val endTime: String = "",
    val unitName: String = "",
    val venue: String = "",
    val lecturer: String = "",
    val dayId: String = ""
)

data class Subjects(
    val id: String = MyDatabase.generateSubjectsID(),
    val name: String = "",

    )

data class Assignment(
    val id: String = MyDatabase.generateAssignmentID(),
    val name: String = "",
    val description: String = "",
    val dueDate: String = "",
    val subjectId: String = ""
)

data class Day(
    val id: String = MyDatabase.generateDayID(),
    val name: String = ""
)

data class Announcement(
    val id: String = MyDatabase.generateAnnouncementID(),
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = ""


)

object MyDatabase {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    //initialize the Unique id of the items
    private var userID = 0
    private var announcementID = 0
    private var timetableID = 0
    private var assignmentID = 0
    private var subjectsID = 0
    private var dayID = 0

    private var calendar: Calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)

    // index number
    fun generateIndexNumber(): String {
        val currentID = userID
        userID++
        return "CP$currentID$year"
    }

    fun generateAnnouncementID(): String {
        val currentID = announcementID
        announcementID++
        return "AN$currentID$year"
    }

    fun generateTimetableID(): String {
        val currentID = timetableID
        timetableID++
        return "TT$currentID$year"
    }

    fun generateAssignmentID(): String {
        val currentID = assignmentID
        assignmentID++
        return "AS$currentID$year"
    }

    fun generateSubjectsID(): String {
        val currentID = subjectsID
        subjectsID++
        return "SB$currentID"
    }

    fun generateDayID(): String {
        val currentID = dayID
        dayID++
        return "DY$currentID$year"
    }


    // Write Announcement to the database
    fun writeUsers(user: User) {
        database.child("Users").child(user.id).setValue(user)
    }

    // Retrieve Announcement data from the database
    fun getUsers(onUsersFetched: (List<User>?) -> Unit) {
        database.child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                onUsersFetched(users)
            }

            override fun onCancelled(error: DatabaseError) {
                onUsersFetched(null)
            }
        })
    }

    fun writeTimetable(timetable: Timetable, onComplete: (Boolean) -> Unit) {
        database.child("Timetable").child(timetable.id).setValue(timetable)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Retrieve Announcement data from the database
    fun getTimetable(dayId: String, onAssignmentsFetched: (List<Timetable>?) -> Unit) {
        database.child("Timetable").orderByChild("dayId").equalTo(dayId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val timetable =
                        snapshot.children.mapNotNull { it.getValue(Timetable::class.java) }
                    onAssignmentsFetched(timetable)
                }

                override fun onCancelled(error: DatabaseError) {
                    onAssignmentsFetched(null)
                }
            })
    }

    fun editTimetable(timetable: Timetable, onComplete: (Boolean) -> Unit) {
        database.child("Timetable").child(timetable.id).setValue(timetable)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun deleteTimetable(timetableId: String, onComplete: (Boolean) -> Unit) {
        database.child("Timetable").child(timetableId).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun editSubject(subject: Subjects, onComplete: (Boolean) -> Unit) {
        val subjectRef = database.child("Subjects").child(subject.id)
        subjectRef.setValue(subject).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun writeSubject(subject: Subjects, onComplete: (Boolean) -> Unit) {
        database.child("Subjects").child(subject.id).setValue(subject)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getSubjects(onSubjectsFetched: (List<Subjects>?) -> Unit) {
        database.child("Subjects").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val subjects = snapshot.children.mapNotNull { it.getValue(Subjects::class.java) }
                onSubjectsFetched(subjects)
            }

            override fun onCancelled(error: DatabaseError) {
                onSubjectsFetched(null)
            }
        })
    }

    fun writeDays(day: Day, onComplete: (Boolean) -> Unit) {
        database.child("Days").child(day.id).setValue(day)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getDays(onSubjectsFetched: (List<Day>?) -> Unit) {
        database.child("Days").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val days = snapshot.children.mapNotNull { it.getValue(Day::class.java) }
                onSubjectsFetched(days)
            }

            override fun onCancelled(error: DatabaseError) {
                onSubjectsFetched(null)
            }
        })
    }

    fun writeAssignment(assignment: Assignment, onComplete: (Boolean) -> Unit) {
        database.child("Assignments").child(assignment.id).setValue(assignment)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getAssignments(subjectId: String, onAssignmentsFetched: (List<Assignment>?) -> Unit) {
        database.child("Assignments").orderByChild("subjectId").equalTo(subjectId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val assignments =
                        snapshot.children.mapNotNull { it.getValue(Assignment::class.java) }
                    onAssignmentsFetched(assignments)
                }

                override fun onCancelled(error: DatabaseError) {
                    onAssignmentsFetched(null)
                }
            })
    }

    fun deleteAssignment(assignmentId: String, onComplete: (Boolean) -> Unit) {
        database.child("Assignments").child(assignmentId).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun editAssignment(assignment: Assignment, onComplete: (Boolean) -> Unit) {
        database.child("Assignments").child(assignment.id).setValue(assignment)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }


    fun writeAnnouncement(announcement: Announcement) {
        database.child("Announcements").child(announcement.id).setValue(announcement)
    }

    // Retrieve Announcement data from the database
    fun getAnnouncements(onUsersFetched: (List<Announcement>?) -> Unit) {
        database.child("Announcements").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val announcements =
                    snapshot.children.mapNotNull { it.getValue(Announcement::class.java) }
                onUsersFetched(announcements)
            }

            override fun onCancelled(error: DatabaseError) {
                onUsersFetched(null)
            }
        })
    }

    fun deleteAnnouncement(announcementId: String) {
        database.child("Announcements").child(announcementId.toString()).removeValue()
    }
}

