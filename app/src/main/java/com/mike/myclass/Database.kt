package com.mike.myclass

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Calendar
import java.util.Locale
import java.util.UUID


open class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    var isAdmin: Boolean = true
)

data class GridItem(
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val link: String = "",
    var fileType: String = "image"
)


enum class Section {
    NOTES, PAST_PAPERS, RESOURCES
}

data class Message(
    var id: String = "",
    var message: String = "",
    var senderName: String = "",
    var senderID: String = "",
    var time: String = "",
    var date: String = "",
    var recipientID: String = ""

)

data class Timetable(
    val id: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val unitName: String = "",
    val venue: String = "",
    val lecturer: String = "",
    val dayId: String = ""
)

data class Feedback(
    val id: String = "",
    val rating: Int = 0,
    val message: String = "",
    val sender: String = "",
    val admissionNumber: String = ""
)

data class Student(
    val id: String = "",
    val firstName: String
)

data class AttendanceRecord(
    val studentId: String = "",
    val dayOfWeek: String = "",
    val isPresent: Boolean = false,
    val lesson: String = ""
)

data class AttendanceState(
    val courseID: String = "",
    val courseName: String = "",
    val state: Boolean = false
)


data class Assignment(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val dueDate: String = "",
    val courseCode: String = ""
)

data class Day(
    val id: String = "",
    val name: String = ""
)

data class Announcement(
    val id: String = "",
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = ""
)

data class Fcm(
    val id: String = "",
    val token: String = ""
)

data class Course(
    val courseCode: String = "",
    val courseName: String = "",
    var lastDate: String = ""

)
data class Chat(
    var id: String = "",
    var message: String = "",
    var senderName: String = "",
    var time: String = "",
    var date: String = "",
    var senderID: String = ""

    )
data class MyCode(
    val id: String = UUID.randomUUID().toString(),
    var code: Int = 0
)


object MyDatabase {
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var calendar: Calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)


    fun generateIndexNumber(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "CP$newCode$year"
            onIndexNumberGenerated(indexNumber) 
        }
    }


    fun generateChatID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "CH$newCode$year"
            onIndexNumberGenerated(indexNumber) 
        }
    }

    fun generateFCMID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "FC$newCode$year"
            onIndexNumberGenerated(indexNumber) 
        }
    }

    fun generateFeedbackID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "FB$newCode$year"
            onIndexNumberGenerated(indexNumber) 
        }
    }

    private fun generateAttendanceID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "AT$newCode$year"
            onIndexNumberGenerated(indexNumber) 
        }
    }

     fun generateAnnouncementID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "AN$newCode$year"
            onIndexNumberGenerated(indexNumber) 
        }
    }

     fun generateTimetableID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "TT$newCode$year"
            onIndexNumberGenerated(indexNumber) 
        }
    }

     fun generateAssignmentID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "AS$newCode$year"
            onIndexNumberGenerated(indexNumber) 
        }
    }

     fun generateDayID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "DY$newCode$year"
            onIndexNumberGenerated(indexNumber) 
        }
    }

    private fun updateAndGetCode(onCodeUpdated: (Int) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("Code")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val myCode = snapshot.getValue(MyCode::class.java)!!
                    myCode.code += 1
                    database.setValue(myCode).addOnSuccessListener {
                        onCodeUpdated(myCode.code) // Pass the incremented code to the callback
                    }
                } else {
                    val newCode = MyCode(code = 1)
                    database.setValue(newCode).addOnSuccessListener {
                        onCodeUpdated(newCode.code) // Pass the initial code to the callback
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error appropriately (e.g., log it or notify the user)
            }
        })
    }

    fun sendMessage(chat: Chat, onComplete: (Boolean) -> Unit) {
        database.child("Chats").push().setValue(chat).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun fetchChats(onChatsFetched: (List<Chat>) -> Unit) {
        database.child("Chats").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = snapshot.children.mapNotNull { it.getValue(Chat::class.java) }
                onChatsFetched(chats)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun sendUserToUserMessage(message: Message, path: String, onComplete: (Boolean) -> Unit) {
        database.child(path).push().setValue(message).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun fetchUserToUserMessages(path: String, onMessagesFetched: (List<Message>) -> Unit) {
        database.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                onMessagesFetched(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    fun fetchUserDataByAdmissionNumber(admissionNumber: String, callback: (User?) -> Unit) {
        database.child("Users").orderByChild("id").equalTo(admissionNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.child("id").getValue(String::class.java)
                        if (userId == admissionNumber) {
                            val userEmail = userSnapshot.child("Email").getValue(String::class.java) ?: ""
                            val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: ""
                            val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: ""
                            val phoneNumber = userSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                            callback(User(id = userId, firstName = firstName, lastName  =lastName, phoneNumber = phoneNumber, email = userEmail))
                            return
                        }
                    }
                    callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null) // Handle or log the error as needed
                }
            }
            )
    }

    fun fetchUserDataByEmail(email: String, callback: (User?) -> Unit) {
        database.child("Users").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        val userEmail = userSnapshot.child("email").getValue(String::class.java)
                        if (userEmail == email) {
                            val userId = userSnapshot.child("id").getValue(String::class.java) ?: ""
                            val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: ""
                            val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: ""
                            val phoneNumber = userSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                            callback(User(id = userId, firstName = firstName,lastName = lastName,phoneNumber = phoneNumber, email = userEmail))
                            return
                        }
                    }
                    callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null) // Handle or log the error as needed
                }
            })
    }



    fun fetchCourses(onCoursesFetched: (List<Course>) -> Unit) {
        database.child("Courses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courseList = snapshot.children.mapNotNull { it.getValue(Course::class.java) }
                onCoursesFetched(courseList) // Call the callback with the fetched courses
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, maybe pass an empty list or an error state to the callback
                onCoursesFetched(emptyList())
            }
        })
    }

    fun writeItem(courseId: String, section: Section, item: GridItem) {
        database.child("Courses").child(courseId).child(section.name).push().setValue(item)
            .addOnSuccessListener {
                // Data successfully written
            }
            .addOnFailureListener { exception ->
                // Handle the write error
            }
    }

    fun readItems(courseId: String, section: Section, onItemsRead: (List<GridItem>) -> Unit) {
        database.child("Courses").child(courseId).child(section.name).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(GridItem::class.java) }
                onItemsRead(items)
            }

            override fun onCancelled(error: DatabaseError) {
                onItemsRead(emptyList())
            }
        })
    }

    fun deleteItem(courseId: String, section: Section, item: GridItem) {
        val itemsRef = database.child("Courses").child(courseId).child(section.name)
        itemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    if (child.getValue(GridItem::class.java) == item) {
                        child.ref.removeValue()
                            .addOnSuccessListener {
                                // Item successfully deleted
                            }
                            .addOnFailureListener { exception ->
                                // Handle the deletion error
                            }
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the read error
            }
        })
    }

    fun updatePassword(newPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.updatePassword(newPassword)?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { exception -> onFailure(exception) }
    }

    fun fetchAverageRating(onAverageRatingFetched: (String) -> Unit) {
        val feedbackRef = database.child("Feedback")
        feedbackRef.get().addOnSuccessListener { snapshot ->
            var totalRating = 0.0
            var count = 0

            for (childSnapshot in snapshot.children) {
                val feedback = childSnapshot.getValue(Feedback::class.java)
                feedback?.rating?.let {
                    totalRating += it
                    count++
                }
            }

            val averageRating = if (count > 0) totalRating / count else 0.0
            val formattedAverage = String.format(Locale.US, "%.1f", averageRating)
            onAverageRatingFetched(formattedAverage)
        }.addOnFailureListener {
            onAverageRatingFetched(String.format(Locale.US, "%.1f", 0.0))
        }
    }


    fun updateUser(userId: String, newFirstName: String, newLastName: String, onSuccess: () -> Unit, onFailure: (Exception?) -> Unit) {
        val userRef = database.child("Users").child(userId)
        val updatedUser = User(firstName = newFirstName, lastName = newLastName )
        userRef.setValue(updatedUser)
            .addOnSuccessListener {
                onSuccess() // Callback on successful update
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Callback on failure with exception
            }
    }

    fun deleteUser(userId: String, onSuccess: () -> Unit, onFailure: (Exception?) -> Unit) {
        database.child("Users").child(userId).removeValue()
            .addOnSuccessListener {
                onSuccess() // Callback on successful deletion
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Callback on failure with exception
            }
    }

    fun writeUsers(user:User, onComplete: (Boolean) -> Unit) {
        database.child("Users").child(user.id).setValue(user)
            .addOnSuccessListener {
                onComplete(true) // Success: invoke callback with 'true'
            }
            .addOnFailureListener {
                onComplete(false) // Failure: invoke callback with 'false'
            }
    }

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

    fun writeFeedback(feedback: Feedback, onSuccess: () -> Unit, onFailure: (Exception?) -> Unit) {
        val feedbackRef = database.child("Feedback").child(feedback.id)
        feedbackRef.setValue(feedback)
            .addOnSuccessListener {
                onSuccess() // Callback on successful write
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Callback on failure with exception
            }
    }

    //send the token to the database
    fun writeFcmToken(token: Fcm) {
        database.child("FCM").child(token.id).setValue(token)
    }


    fun writeTimetable(timetable: Timetable, onComplete: (Boolean) -> Unit) {
        database.child("Timetable").child(timetable.id).setValue(timetable)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

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

    fun getCurrentDayTimetable(dayName: String, onTimetableFetched: (List<Timetable>?) -> Unit) {
        // Step 1: Fetch the dayId from the Day node using the dayName
        database.child("Days").orderByChild("name").equalTo(dayName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dayId = snapshot.children.firstOrNull()?.key

                    if (dayId != null) {
                        // Step 2: Use the fetched dayId to query the Timetable node
                        database.child("Timetable").orderByChild("dayId").equalTo(dayId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val timetable =
                                        snapshot.children.mapNotNull { it.getValue(Timetable::class.java) }
                                    onTimetableFetched(timetable)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    onTimetableFetched(null)
                                }
                            })
                    } else {
                        // Day not found
                        onTimetableFetched(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onTimetableFetched(null)
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
        database.child("Timetable").child(timetableId).removeValue().addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }


    fun writeDays(day: Day, onComplete: (Boolean) -> Unit) {
        database.child("Days").child(day.id).setValue(day).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun getDays(onCoursesFetched: (List<Day>?) -> Unit) {
        database.child("Days").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val days = snapshot.children.mapNotNull { it.getValue(Day::class.java) }
                onCoursesFetched(days)
            }

            override fun onCancelled(error: DatabaseError) {
                onCoursesFetched(null)
            }
        })
    }

    fun writeAssignment(assignment: Assignment, onComplete: (Boolean) -> Unit) {
        database.child("Assignments").child(assignment.id).setValue(assignment)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getAssignments(courseCode: String, onAssignmentsFetched: (List<Assignment>?) -> Unit) {
        database.child("Assignments").orderByChild("courseCode").equalTo(courseCode)
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

    fun loadCourseAndAssignments(callback: (List<Course>?) -> Unit) {
        database.child("Courses").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val courseList = task.result.children.mapNotNull { dataSnapshot ->
                    val course = dataSnapshot.getValue(Course::class.java)
                    if (course?.courseName.isNullOrEmpty()) {
                        Log.e("DataFetch", "Course with missing name: $dataSnapshot")
                        null
                    } else {
                        course
                    }
                }
                callback(courseList)
            } else {
                Log.e("DataFetch", "Error fetching courses: ${task.exception?.message}")
                callback(null)
            }
        }
    }

    fun loadStudents(onStudentsLoaded: (List<Student>?) -> Unit) {
        database.child("Students").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val students = snapshot.children.mapNotNull {
                    val id = it.child("id").getValue(String::class.java)
                    val firstName = it.child("firstName").getValue(String::class.java)
                    if (id != null && firstName != null) {
                        Student(id, firstName)
                    } else null
                }
                onStudentsLoaded(students)
            }

            override fun onCancelled(error: DatabaseError) {
                onStudentsLoaded(null)
            }
        })
    }

    fun loadAttendanceRecords(onAttendanceRecordsLoaded: (List<AttendanceRecord>?) -> Unit) {
        database.child("attendanceRecords")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val attendanceRecords = snapshot.children.mapNotNull {
                        val studentId = it.child("studentId").getValue(String::class.java)
                        val dayOfWeek = it.child("dayOfWeek").getValue(String::class.java)
                        val isPresent = it.child("isPresent").getValue(Boolean::class.java)
                        val lesson = it.child("lesson").getValue(String::class.java)
                        if (studentId != null && dayOfWeek != null && isPresent != null && lesson != null) {
                            AttendanceRecord(studentId, dayOfWeek, isPresent, lesson)
                        } else null
                    }
                    onAttendanceRecordsLoaded(attendanceRecords)
                }

                override fun onCancelled(error: DatabaseError) {
                    onAttendanceRecordsLoaded(null)
                }
            }
        )
    }

    fun saveAttendanceState(attendanceState: AttendanceState) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("AttendanceStates").child(attendanceState.courseID).setValue(attendanceState)
    }


    fun fetchAttendanceState(courseCode: String, onStateFetched: (AttendanceState?) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("AttendanceStates").child(courseCode).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val attendanceState = snapshot.getValue(AttendanceState::class.java)
                onStateFetched(attendanceState) // Pass the fetched state or null if not found
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, maybe pass null to indicate failure
                onStateFetched(null)
            }
        })
    }

    //fetch the day id using the day name
    fun getDayIdByName(dayName: String, onDayIdFetched: (String?) -> Unit) {
        database.child("Days").orderByChild("name").equalTo(dayName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dayId = snapshot.children.firstOrNull()?.key
                    onDayIdFetched(dayId)
                }

                override fun onCancelled(error: DatabaseError) {
                    onDayIdFetched(null)
                }
            }
        )
    }

    fun saveAttendanceRecords(records: List<AttendanceRecord>, onComplete: (Boolean) -> Unit) {
        val batch = database.child("attendanceRecords")
        records.map { record ->
            val key = batch.push().key ?: ""
            batch.child(key).setValue(record)
        }
    }
}

// Function to save messages to a file
fun saveMessagesToFile(context: Context, messages: List<Message>, fileName: String) {
    val gson = Gson()
    val jsonString = gson.toJson(messages)
    val file = File(context.filesDir, fileName)
    file.writeText(jsonString)
}

// Function to load messages from a file
fun loadMessagesFromFile(context: Context, fileName: String): List<Message> {
    val file = File(context.filesDir, fileName)
    if (!file.exists()) return emptyList()
    val jsonString = file.readText()
    val gson = Gson()
    val type = object : TypeToken<List<Message>>() {}.type
    return gson.fromJson(jsonString, type)
}

fun saveChatsToFile(context: Context, chats: List<Chat>, fileName: String) {
    val gson = Gson()
    val jsonString = gson.toJson(chats)
    val file = File(context.filesDir, fileName)
    file.writeText(jsonString)
}

// Function to load messages from a file
fun loadChatsFromFile(context: Context, fileName: String): List<Chat> {
    val file = File(context.filesDir, fileName)
    if (!file.exists()) return emptyList()
    val jsonString = file.readText()
    val gson = Gson()
    val type = object : TypeToken<List<Chat>>() {}.type
    return gson.fromJson(jsonString, type)
}

