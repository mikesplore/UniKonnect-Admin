package com.mike.myclass

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.Coil
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import com.mike.myclass.CommonComponents as CC

data class Profile(
    var name: String = "Michael Odhiambo",
    var fieldOfStudy: String = "Native Android Developer",
    var phoneNumber: String = "0799013845",
    var email: String = "mikepremium8@gmail.com",
    var bio: String = "Victory is a state of mind"
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, context: Context) {
    val profile = Profile()
    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBackIosNew, "back", tint = GlobalColors.textColor
                    )
                }
            }, title = {}, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GlobalColors.primaryColor,
                titleContentColor = GlobalColors.textColor
            )
            )
        }, containerColor = GlobalColors.primaryColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Box(
                modifier = Modifier
                    .background(
                        GlobalColors.secondaryColor, RoundedCornerShape(0.dp, 0.dp, 30.dp, 30.dp)
                    )
                    .height(200.dp)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                //the inner column that has the profile and the bio
                TopProfile()

                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .offset(y = 145.dp)
                        .background(
                            brush = CC.backbrush, shape = RoundedCornerShape(10.dp)
                        )
                        .fillMaxWidth(0.9f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 1.dp,
                                color = GlobalColors.tertiaryColor,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(profile.fieldOfStudy, style = CC.titleTextStyle(context))
                            Spacer(modifier = Modifier.height(8.dp)) // Add spacing between elements

                            // Use a Row for better visual alignment of contact info
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = GlobalColors.tertiaryColor)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(profile.phoneNumber, style = CC.descriptionTextStyle(context))
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = GlobalColors.tertiaryColor)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(profile.email, style = CC.descriptionTextStyle(context))
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Bio", style = CC.descriptionTextStyle(context).copy(fontWeight = FontWeight.Bold)) // Subtle emphasis for the bio title

                            Text(
                                profile.bio,
                                style = CC.descriptionTextStyle(context),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun TopProfile() {
    var name by remember { mutableStateOf("Michael Odhiambo") }
    var bio by remember { mutableStateOf("Bio goes here...") }
    var profileImageUri by remember { mutableStateOf<String?>(null) }
    var backImageUri by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    val request = ImageRequest.Builder(context).data(it)
                        .transformations(CircleCropTransformation()).allowHardware(false)
                        .diskCachePolicy(CachePolicy.DISABLED).target { drawable ->
                            val bitmap = (drawable as BitmapDrawable).bitmap
                            profileImageUri = saveImageToInternalStorage(context, bitmap)
                        }.build()
                    // Execute the request
                    Coil.imageLoader(context).enqueue(request)
                }
            }
        }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        launcher.launch(intent)
    }
    Box {
        if (backImageUri == null) {
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(backImageUri),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(if (profileImageUri == null) Color.Gray else Color.Transparent),

                ) {
                if (profileImageUri == null) {
                    Image(
                        painter = painterResource(R.drawable.student),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUri),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Box(modifier = Modifier
                    .background(GlobalColors.primaryColor, CircleShape)
                    .clickable { selectImage() }
                    .absolutePadding(0.dp, 0.dp, 10.dp, 10.dp)
                    .size(20.dp)
                    .clip(CircleShape), contentAlignment = Alignment.Center

                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = GlobalColors.textColor,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(20.dp)

                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = name.uppercase(java.util.Locale.ROOT),
                style = CC.titleTextStyle(context),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }


}

fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): String {
    val filename = "profile_image.png"
    val file = File(context.filesDir, filename)
    var fos: OutputStream? = null
    try {
        fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
    } finally {
        fos?.close()
    }
    return file.absolutePath
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController(), context = LocalContext.current)

}