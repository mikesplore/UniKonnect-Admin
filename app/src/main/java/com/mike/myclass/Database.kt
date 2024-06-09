package com.mike.myclass

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

object Details{
    var email: MutableState<String> = mutableStateOf("")
    var name:  MutableState<String> = mutableStateOf("Mike")
}

open class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",

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
                val users =
                    snapshot.children.mapNotNull { it.getValue(User::class.java) }
                onUsersFetched(users)
            }

            override fun onCancelled(error: DatabaseError) {
                onUsersFetched(null)
            }
        })
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

