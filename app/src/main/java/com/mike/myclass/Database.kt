package com.mike.myclass

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID


data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",

    )

data class Subjects(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",

    )

data class Assignment(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val dueDate: String = "",
    val subjectId: String = ""
)

data class Announcement(
    val id: String = UUID.randomUUID().toString(),
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = ""

)

object MyDatabase {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

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
    fun deleteSubject(subjectId: String, onComplete: (Boolean) -> Unit) {
        database.child("Subjects").child(subjectId).removeValue()
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

