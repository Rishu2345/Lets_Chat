package com.example.letschat.screens

 import android.util.Log
 import android.widget.Toast
 import androidx.compose.foundation.ExperimentalFoundationApi
 import androidx.compose.foundation.Image
 import androidx.compose.foundation.background
 import androidx.compose.foundation.border
 import androidx.compose.foundation.clickable
 import androidx.compose.foundation.layout.Arrangement
 import androidx.compose.foundation.layout.Box
 import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.Row
 import androidx.compose.foundation.layout.WindowInsets
 import androidx.compose.foundation.layout.fillMaxHeight
 import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.foundation.layout.fillMaxWidth
 import androidx.compose.foundation.layout.height
 import androidx.compose.foundation.layout.heightIn
 import androidx.compose.foundation.layout.ime
 import androidx.compose.foundation.layout.imePadding
 import androidx.compose.foundation.layout.navigationBarsPadding
 import androidx.compose.foundation.layout.padding
 import androidx.compose.foundation.layout.size
 import androidx.compose.foundation.layout.statusBarsPadding
 import androidx.compose.foundation.layout.widthIn
 import androidx.compose.foundation.lazy.LazyColumn
 import androidx.compose.foundation.lazy.items
 import androidx.compose.foundation.lazy.rememberLazyListState
 import androidx.compose.foundation.shape.CircleShape
 import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.Clear
 import androidx.compose.material.icons.filled.Close
 import androidx.compose.material.icons.filled.Delete
 import androidx.compose.material.icons.filled.Face
 import androidx.compose.material.icons.filled.MoreVert
 import androidx.compose.material.icons.filled.Person
 import androidx.compose.material.icons.filled.Send
 import androidx.compose.material3.DropdownMenu
 import androidx.compose.material3.DropdownMenuItem
 import androidx.compose.material3.ExperimentalMaterial3Api
 import androidx.compose.material3.ExposedDropdownMenuBox
 import androidx.compose.material3.ExposedDropdownMenuDefaults
 import androidx.compose.material3.Icon
 import androidx.compose.material3.IconButton
 import androidx.compose.material3.MaterialTheme
 import androidx.compose.material3.OutlinedTextField
 import androidx.compose.material3.Text
 import androidx.compose.material3.TextField
 import androidx.compose.runtime.Composable
 import androidx.compose.runtime.LaunchedEffect
 import androidx.compose.runtime.collectAsState
 import androidx.compose.runtime.derivedStateOf
 import androidx.compose.runtime.getValue
 import androidx.compose.runtime.mutableStateListOf
 import androidx.compose.runtime.mutableStateOf
 import androidx.compose.runtime.remember
 import androidx.compose.runtime.rememberCoroutineScope
 import androidx.compose.runtime.setValue
 import androidx.compose.ui.Alignment
 import androidx.compose.ui.Modifier
 import androidx.compose.ui.draw.clip
 import androidx.compose.ui.geometry.CornerRadius
 import androidx.compose.ui.geometry.Rect
 import androidx.compose.ui.geometry.RoundRect
 import androidx.compose.ui.geometry.Size
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.graphics.Outline
 import androidx.compose.ui.graphics.Path
 import androidx.compose.ui.graphics.Shape
 import androidx.compose.ui.layout.ContentScale
 import androidx.compose.ui.platform.LocalContext
 import androidx.compose.ui.platform.LocalDensity
 import androidx.compose.ui.res.painterResource
 import androidx.compose.ui.text.font.FontStyle
 import androidx.compose.ui.text.font.FontWeight
 import androidx.compose.ui.tooling.preview.Preview
 import androidx.compose.ui.unit.Density
 import androidx.compose.ui.unit.Dp
 import androidx.compose.ui.unit.LayoutDirection
 import androidx.compose.ui.unit.dp
 import androidx.compose.ui.unit.sp
 import androidx.compose.ui.zIndex
 import androidx.navigation.NavHostController
 import coil.compose.AsyncImage
 import com.example.letschat.AppDatabase
 import com.example.letschat.FirestoreFunction
 import com.example.letschat.MainViewModel
 import com.example.letschat.R
 import com.example.letschat.RealTimeDatabaseFunction
 import com.example.letschat.ui.theme.background
 import com.example.letschat.ui.theme.primary
 import com.example.letschat.ui.theme.secondary
 import kotlinx.coroutines.launch
 import okhttp3.Dispatcher
 import java.time.Instant
 import java.time.LocalDateTime
 import java.time.ZoneId
 import com.example.letschat.Messages
 import com.example.letschat.MessageType


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MassagesScreen(modifier: Modifier,viewModel: MainViewModel,navController: NavHostController){
    val userId = viewModel.getUserId() ?: run{navController.navigateUp()}
    var friendsName by remember { mutableStateOf("Stud Phone") }
    var friendId by remember{mutableStateOf("")}
    var pfp by remember{mutableStateOf("")}
    viewModel.getCurrentProfile()?.let{
        Log.d("RemoveFriend", "Current profile: $it")
        friendsName = it.name
        pfp = it.profilePicture ?: ""
        friendId = it.email
    } ?: run{navController.navigateUp()}
    val context = LocalContext.current
    val roomDB = AppDatabase.getDatabase(context).taskDao()
    val massages by viewModel.getMassages(viewModel.getChatId() ?: "").collectAsState(initial = emptyList())
    val massagesGroupByDate = massages.groupBy { LocalDateTime.ofInstant(Instant.ofEpochMilli(it.time), ZoneId.systemDefault()).toLocalDate() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val rtdb = RealTimeDatabaseFunction(context = context)
    var msgText by remember{mutableStateOf("")}
    val added = remember{mutableSetOf<String>()}
    val options = listOf("View Profile", "Clear Chat", "Block")
    var expanded by remember { mutableStateOf(false) }
    val firestoreFunction = FirestoreFunction(viewModel)
    val inset = WindowInsets.ime
    val density = LocalDensity.current
    val isVisible = inset.getBottom(density) > 0

    LaunchedEffect(massages.size) {
        listState.animateScrollToItem(if(massages.isEmpty()) 0 else  massages.size - 1 )
    }



    viewModel.getChatId()?.let{
        rtdb.listenForMessages(it){
            id,sender, text, timestamp ->
            if(added.add(id)) {
                scope.launch {
                    roomDB.insertMessage(
                        Messages(
                            id = id,
                            text = text,
                            type = if (sender == userId) MessageType.SENT else MessageType.RECEIVED,
                            time = timestamp,
                            chatId = it
                        )
                    )
                }
            }
        }

    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(primary)
        .zIndex(0f)
    ){
        Image(
            painter = painterResource(R.drawable.massaging_background),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentScale = ContentScale.Crop
        )
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .zIndex(1f)
        .then(modifier))
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(primary)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            AsyncImage(
                model = pfp,
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )

            Text(
                text = friendsName,
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
            )



            Box {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                expanded = false
                                when (option) {
                                    "View Profile" -> {
                                        Toast.makeText(context, "🚧 Under Construction ", Toast.LENGTH_SHORT).show()
                                    }
                                    "Clear Chat" -> {
                                        scope.launch {
                                            roomDB.deleteAllChat(viewModel.getChatId() ?:"")
                                        }
                                        Toast.makeText(context, "Chat cleared", Toast.LENGTH_SHORT).show()
                                    }
                                    "Block" -> {
                                        rtdb.deleteChat(viewModel.getChatId() ?: "")
                                        firestoreFunction.removeFriend(userId as String,friendId){}
                                        navController.navigateUp()
                                    }
                                }
                            },
                            leadingIcon = {
                                when (option) {
                                    "View Profile" -> Icon(Icons.Default.Person, contentDescription = null)
                                    "Clear Chat" -> Icon(Icons.Default.Clear, contentDescription = null)
                                    "Block" -> Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        )
                    }
                }
            }

        }
        //The Drop Down menu

        LazyColumn(
            state = listState,
            reverseLayout = false,
            modifier = Modifier
                .fillMaxHeight(if(isVisible) 0.84f else 0.9f)
                .fillMaxWidth()
        ) {
            massagesGroupByDate.forEach { (date, messages) ->
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = viewModel.formateDate(date),
                            color = Color.Black,
                            modifier = Modifier
                                .background(Color.LightGray, RoundedCornerShape(5.dp))
                                .padding(horizontal = 4.dp,vertical = 2.dp)
                                .align(Alignment.Center)
                                ,
                            fontSize = 12.sp,
                        )
                    }
                }

                items(messages) { message ->
                    ChatBubble(
                        text = message.text,
                        type = message.type,
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(50.dp)
        )
        {
            OutlinedTextField(value = msgText,
                onValueChange = {msgText = it},
                modifier = Modifier
                    .fillMaxWidth(0.84f)
                    .height(50.dp)
                    .background(background, RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                leadingIcon = {Icon(imageVector = Icons.Default.Face,
                    contentDescription = "Emoji",
                    modifier = Modifier
                        .clickable{}

                )},
                placeholder = {Text(text = "Enter Your Message Here")},


            )

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 5.dp)
                .clip(CircleShape)
                .background(secondary)
                .clickable{
                    viewModel.getChatId()?.let{
                        chatId ->
                        viewModel.getUserId()?.let{
                            userId->
                            rtdb.sendMessage(chatId = chatId,senderId = userId,msgText){
                                Log.d("MassageScreen","sent Successfully")
                                msgText = ""
                            }
                        }?: Toast.makeText(context,"UserId is null",Toast.LENGTH_SHORT).show()
                    } ?: Toast.makeText(context,"ChatId is null",Toast.LENGTH_SHORT).show()

                }

            ){
                Icon(imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(28.dp),
                    tint = background
                )


            }
        }

    }




}
class SpeechBubbleShape(
    private val isSentByMe: Boolean,
    private val cornerRadius: Dp = 10.dp,
    private val tailWidth: Dp = 10.dp,
    private val tailHeight: Dp = 5.dp
) : Shape
{
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = with(density) {
        val r = with(density){cornerRadius.toPx()}
        val tW = with(density) { tailWidth.toPx() }
        val tH = with(density) { tailHeight.toPx() }

        val path = Path().apply {
            if (isSentByMe) {

                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, 0f, size.width - tW, size.height - tH),
                        topLeft = CornerRadius(r, r),
                        topRight = CornerRadius(r, r),
                        bottomRight = CornerRadius(0f, 0f),
                        bottomLeft = CornerRadius(r, r)
                    )
                )

                val cx = size.width - tW
                val cy = size.height
                moveTo(cx, cy - r - tH)
                lineTo(cx,cy-tH)
                lineTo(cx-r,cy-tH)
                lineTo(size.width, size.height)
                close()
            } else {
                addRoundRect(
                    RoundRect(
                        rect = Rect(tW, 0f, size.width, size.height - tH),
                        topLeft = CornerRadius(r, r),
                        topRight = CornerRadius(r, r),
                        bottomRight = CornerRadius(r, r),
                        bottomLeft = CornerRadius(r, r)
                    )
                )

                val cx = tW
                val cy = size.height
                moveTo(cx, cy - r - tH)
                lineTo(0f, cy)
                lineTo(cx + r, cy - r / 2f)
                close()
            }
        }
        Outline.Generic(path)
    }
}

@Composable
fun ChatBubble(
    text: String,
    type: String,
    modifier: Modifier = Modifier
) {
    val paddingModifier by remember{ derivedStateOf {
        when(type){
            MessageType.SENT -> Modifier.padding(start = 10.dp, end = 15.dp, top = 5.dp, bottom = 8.dp)
            MessageType.RECEIVED -> Modifier.padding(start = 15.dp, end = 8.dp, top = 5.dp, bottom = 8.dp)
            else -> Modifier
        }


    } }
    val bg = if (type == MessageType.SENT) Color(0xFF2196F3) else Color(0xFFE0E0E0)
    val textColor = if (type == MessageType.SENT) Color.White else Color.Black
    Box(modifier = Modifier
        .fillMaxWidth()
    ){
        if(type == MessageType.SYSTEM){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = text,
                    color = Color.Black,
                    modifier = Modifier
                        .background(Color.LightGray, RoundedCornerShape(5.dp))
                        .padding(horizontal = 4.dp,vertical = 2.dp)
                        .align(Alignment.Center)
                    ,
                    fontSize = 12.sp,
                )
            }
        }else {
            Box(
                modifier = modifier
                    .background(color = bg, shape = SpeechBubbleShape(type == MessageType.SENT))
                    .widthIn(min = 20.dp, max = 200.dp)

                    .align(if(type == MessageType.SENT) Alignment.CenterEnd else Alignment.CenterStart)
                    .then(paddingModifier)

            ) {
                Text(text = text, color = textColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

}



@Composable
fun ObserveKeyboardVisibility(onVisibilityChanged: (Boolean) -> Unit) {
    val insets = WindowInsets.ime
    val density = LocalDensity.current
    val imeBottom = insets.getBottom(density)

    val isVisible = imeBottom > 0

    LaunchedEffect(isVisible) {
        onVisibilityChanged(isVisible)
    }
}



@Preview
@Composable
fun ChatBubblePreview() {
//    MassagesScreen(Modifier)
//    ChatBubble(text = "Hello, how are you? i am fine thank you ", type = MassageType.RECEIVED)
}

