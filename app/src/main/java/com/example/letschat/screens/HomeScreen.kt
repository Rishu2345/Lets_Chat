package com.example.letschat.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.letschat.FirestoreFunction
import com.example.letschat.Friend
import com.example.letschat.MainViewModel
import com.example.letschat.R
import com.example.letschat.RealTimeDatabaseFunction
import com.example.letschat.msgScreen
import com.example.letschat.ui.theme.background
import com.example.letschat.ui.theme.primary
import com.example.letschat.ui.theme.secondary
import kotlinx.coroutines.launch

@Composable
fun HomeScreenWithSideBar(modifier: Modifier,navController: NavHostController,viewModel: MainViewModel) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var profilePhoto by remember { mutableStateOf("") }
    var uniqueId by remember { mutableStateOf("") }
    val firestoreFunction = FirestoreFunction(viewModel)
    Log.d("HomeScreen","Started")
    LaunchedEffect(key1 = Unit) {
        Log.d("HomeScreen","Launched Effect")
        viewModel.getUserId()?.let{
            Log.d("HomeScreen","get the userId $it")
            firestoreFunction.fetchProfile(it,
                success = {profile->
                    name = profile.name
                    uniqueId = profile.uniqueId
                    profilePhoto = profile.profilePicture
                    Log.d("HomeScreen","Profile Fetched $profile")


                },
                failure = { exp ->
                    Log.e("HomeScreen",exp.toString())
                }
            )
        }

    }
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(profilePhoto)
            .placeholder(R.drawable.stud_phone)
            .error(R.drawable.stud_phone)
            .build()
    )

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet()
            {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(0.7f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Spacer(Modifier.height(12.dp))


                    Image(painter = painter, contentDescription = "" ,modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Black, CircleShape),
                        contentScale = ContentScale.FillBounds)
                    Text(text = name, modifier = Modifier.padding(2.dp), style = MaterialTheme.typography.titleMedium)
                    Text(text = uniqueId, modifier = Modifier, style = MaterialTheme.typography.titleMedium , fontSize = 14.sp)
                    Spacer(Modifier.height(20.dp))


                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray.copy(0.8f)))


                    NavigationDrawerItem(
                        label = { Text("Theme") },
                        selected = false,
                        onClick = { Toast.makeText(context,"Under Construction",Toast.LENGTH_SHORT).show() },
                        icon = { Icon(Icons.Outlined.AccountBox, contentDescription = null) }
                    )
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray.copy(0.8f)))


                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray.copy(0.8f)))


                    NavigationDrawerItem(
                        label = { Text("Profile") },
                        selected = false,
                        onClick = { Toast.makeText(context,"Under Construction",Toast.LENGTH_SHORT).show()},
                        icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) }
                    )
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray.copy(0.8f)))

                    NavigationDrawerItem(
                        label = { Text("About Me") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Face, contentDescription = null) },
                        onClick = { Toast.makeText(context,"Under Construction",Toast.LENGTH_SHORT).show() }
                    )
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray.copy(0.8f)))

                    NavigationDrawerItem(
                        label = { Text("Sign Out") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.ExitToApp, contentDescription = null) },
                        onClick = {
                            scope.launch {

                            } },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        HomeScreen(modifier,viewModel,navController)
    }
}
@Composable
fun HomeScreen(modifier: Modifier,viewModel: MainViewModel,navController: NavHostController){
    val context = LocalContext.current
    var addingFriend by remember { mutableStateOf(false) }
    val firestoreFunction = FirestoreFunction(viewModel)
    var friend  by remember{ mutableStateOf<List<Friend>>(emptyList())}
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    Log.d("HomeScreen","Started")
    LaunchedEffect(addingFriend){
        firestoreFunction.getFriendsInfoCallback(
            userId = viewModel.getUserId()!!,
            callback = {
                friend= it
                Log.d("HomeScreen","Friends Fetched $it")
            },
            onError = {
                Log.e("HomeScreen","Error Fetching Friends $it")
                Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
            }
        )
    }
    Log.d("HomeScreen","Friend List $friend")

    Box(modifier = Modifier
        .fillMaxSize()
        .then(modifier))
    {
        Box(modifier = Modifier
            .size(60.dp)
            .align(Alignment.BottomEnd)
            .graphicsLayer{
                translationX = -80f
                translationY = -80f
            }
            .background(primary,CircleShape)
            .clip(CircleShape)
            .clickable {
                addingFriend = true
            }
        ){
            Icon(imageVector = Icons.Rounded.Edit,
                contentDescription = "Add Friend",
                modifier = Modifier
                    .align(Alignment.Center))
        }
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(primary)
        .zIndex(-1f)
        .then(modifier))
    {
        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.08f)
            .background(primary)
            .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }){
                Icon(imageVector = Icons.Default.Menu,
                    contentDescription = "Side Bar Button",
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }
            Text(text = "Let's Chat",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
            )
            IconButton(onClick = {}){
                Icon(imageVector = Icons.Default.Search,
                    contentDescription = "Side Bar Button",
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
        }
        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(background),
            contentPadding = PaddingValues(vertical = 10.dp)) {
            items(friend) {frnd->

                UserBarUi(Modifier,frnd,navController,viewModel)
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .drawBehind {
                        drawLine(
                            color = Color.Gray,
                            start = Offset(size.width/6, size.height),
                            end = Offset(5*size.width/6, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                )
                Spacer(modifier = Modifier.fillMaxWidth().height(5.dp))
            }

        }
    }
    if(addingFriend) {
        NewFriendPopUp(
            modifier.fillMaxSize(), viewModel, back = { addingFriend = false })
    }
}

@Composable
fun UserBarUi(modifier: Modifier,friend: Friend,navController: NavHostController,viewModel: MainViewModel){
    Log.d("HomeScreen","Creating User Bar of $friend")
    var name by remember { mutableStateOf("Stud Phone") }
    var lastMessage by remember { mutableStateOf("Hello") }
    var lastMessageTime by remember { mutableStateOf("10:00") }
    var unreadMassages by remember{mutableIntStateOf(1)}
    friend.let{
        name = it.name
        lastMessage = it.lastMsg
        lastMessageTime = viewModel.formatLocalDateTime(it.lastMsgTime)
        unreadMassages = it.unseenMessages

    }
    val animateSize = animateDpAsState(targetValue = if(unreadMassages > 0) 35.dp else 0.dp,
        animationSpec = spring(Spring.DampingRatioHighBouncy,Spring.StiffnessMedium))

    val model = ImageRequest.Builder(LocalContext.current)
        .data(friend.profilePicture)
        .crossfade(true)
        .build()

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(70.dp)
        .padding(horizontal = 10.dp)
        .clickable{
            viewModel.setChatId(friend.chatId)
            viewModel.setCurrentProfile(friend)
            navController.navigate(msgScreen)
        }
        .then(modifier),
        verticalAlignment = Alignment.CenterVertically)

    {
        AsyncImage(model = model,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(70.dp),
            placeholder = painterResource(R.drawable.stud_phone),
            error = painterResource(R.drawable.stud_phone)
        )
        Column(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.8f)
            .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround)
        {
            Text(text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxWidth()
            )

            Text(text = lastMessage,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxWidth(),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )


        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
            )
        {
            Text(text = lastMessageTime,
                fontSize = 12.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .padding(start = 10.dp)
            )

            Box(modifier = Modifier
                .size(animateSize.value)
                .padding(5.dp)
                .background(secondary,CircleShape)
                .clip(CircleShape)
            ){
                Text(text = unreadMassages.toString(),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }



        }
    }
}

@Composable
fun NewFriendPopUp(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    back: () -> Unit
) {
    var id by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val firestoreFunction = FirestoreFunction(viewModel)
    val realTimeDatabaseFunction = RealTimeDatabaseFunction(context)
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { back() }) {
        Box(
            modifier = Modifier
                .then(modifier)
                .clickable{ back() }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(background, RoundedCornerShape(12.dp))
                    .height(250.dp)
                    .align(alignment = Alignment.Center)
                    .padding(20.dp)
                    .clickable(false){}
            ) {
                Text(
                    text = "Add a New Friend",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("Enter 8-digit Friend ID") },
                    singleLine = true,
                    isError = id.isNotEmpty() && id.length != 8,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )

                if (id.isNotEmpty() && id.length != 8) {
                    Text(
                        text = "Friend ID must be 8 characters",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (id.length == 8) {
                            isLoading = true
                            scope.launch {
                                id = id.trim()
                                viewModel.getUserId()?.let { userEmail ->
                                    firestoreFunction.findUserIdByUniqueCode(id) { email ->
                                        if (email != null) {
                                            firestoreFunction.addFriend(userEmail, email) { chatId ->
                                                if (chatId != null) {
                                                    realTimeDatabaseFunction.createOrGetChatSession(
                                                        chatId,
                                                        userEmail
                                                    ) {
                                                        isLoading = false
                                                        if (it != null) {
                                                            Toast.makeText(
                                                                context,
                                                                "✅ Friend Added Successfully!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            Log.d(
                                                                "NewFriendPopUp",
                                                                "Friend added: $email | ChatId: $chatId"
                                                            )
                                                            back()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "❌ Failed to create chat session",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            Log.e(
                                                                "NewFriendPopUp",
                                                                "Chat session null for $chatId"
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    isLoading = false
                                                    Toast.makeText(
                                                        context,
                                                        "❌ Error creating friend link",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    Log.e(
                                                        "NewFriendPopUp",
                                                        "addFriend() returned null for $email"
                                                    )
                                                }
                                            }
                                        } else {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "⚠️ No user found with that ID",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            Log.w(
                                                "NewFriendPopUp",
                                                "No user found for unique code: $id"
                                            )
                                        }
                                    }
                                } ?: run {
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "⚠️ User not logged in",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("NewFriendPopUp", "UserId was null")
                                }
                            }
                        }
                    },
                    enabled = id.length == 8 && !isLoading,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(45.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = background,
                        disabledContentColor = Color.Black,
                        containerColor = primary,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = "Add Friend",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}


