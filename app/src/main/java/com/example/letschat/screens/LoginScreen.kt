package com.example.letschat.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.letschat.FirestoreFunction
import com.example.letschat.GoogleAuthUIClient
import com.example.letschat.MainViewModel
import com.example.letschat.R
import com.example.letschat.homeScreen
import com.example.letschat.loginScreen
import com.example.letschat.profileScreen
import com.example.letschat.registerScreen
import com.example.letschat.ui.theme.background
import com.example.letschat.ui.theme.textColor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(modifier : Modifier,viewModel: MainViewModel,navHostController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val googleAuthUIClient = GoogleAuthUIClient(context,viewModel)
    val signInText = buildAnnotatedString {
        append("Already have an account?")
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(" Sign in")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = background)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    )
    {
        Image(
            painter = painterResource(id = R.drawable.stud_phone),
            contentDescription = null
        )



        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Enter your email", color = textColor.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Email, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text(text = "Password") },
            placeholder = {
                Text(
                    text = "Enter your Password",
                    color = textColor.copy(alpha = 0.5f)
                )
            },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Lock, contentDescription = null) },
            trailingIcon = {
                Icon(
                    painter = painterResource(if (!showPassword) R.drawable.visibility_24dp_e3e3e3_fill0_wght400_grad0_opsz24__1_ else R.drawable.visibility_off_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                    contentDescription = null,
                    modifier = Modifier.clickable { showPassword = !showPassword })
            },
            supportingText = if (password.value.length > 8) {
                {
                    Text(
                        text = "At least 4 characters and at most 8 characters",
                        color = textColor.copy(alpha = 0.5f)
                    )
                }
            } else null,
            singleLine = true,
            isError = password.value.length > 8,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            value = confirmPassword.value,
            onValueChange = { confirmPassword.value = it },
            label = { Text(text = "Confirm Password") },
            placeholder = {
                Text(
                    text = "Enter your password again ",
                    color = textColor.copy(alpha = 0.5f)
                )
            },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Lock, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Button(
            onClick = {
                if(password.value == confirmPassword.value){
                    googleAuthUIClient.registerWithFirebase(
                        email = email.value,
                        password = password.value,
                        username = "User",
                        onSuccess =
                            {
                                Toast.makeText(context,"Registered Successfully",Toast.LENGTH_LONG).show()
                                viewModel.setUserId(email.value)
                                navHostController.navigate(profileScreen)
                            },
                        onError = {
                            Toast.makeText(context,"Can't Register",Toast.LENGTH_LONG).show()
                        })
                }
                else{
                    Toast.makeText(context,"Wrong Password",Toast.LENGTH_LONG).show()
                }
            },

            modifier = Modifier
                .fillMaxWidth(0.35f)
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 20.dp,
                pressedElevation = 10.dp
            )
        ) {
            Text(
                text = "Submit",
                fontSize = 18.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = textColor.copy(alpha = 0.5f),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 1.dp.toPx()
                    )
                }

        ) {
            Text(
                text = "Or",
                modifier = Modifier
                    .background(background)
                    .width(30.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                color = textColor.copy(alpha = 0.5f),
                fontStyle = FontStyle.Italic
            )
        }

        OutlinedButton(
            onClick = {
                scope.launch {
                    Log.d("signIn","clicked in sign in button")
                    googleAuthUIClient.signIn()?.let{user ->
                        Toast.makeText(context,"Signed In Successfully",Toast.LENGTH_LONG).show()
                        viewModel.setUserId(user.userEmail ?: "")
                        navHostController.navigate(profileScreen)
                    } ?: Toast.makeText(context,"Can't Sign In",Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(45.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_logo_foreground),
                contentDescription = null,
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
            )
            Text(
                text = "Sign in with Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

        }

        Text(
            text = signInText,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navHostController.navigate(loginScreen)
                },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(150.dp).fillMaxWidth())


    }
}

@Composable
fun LoginScreen(modifier: Modifier,viewModel: MainViewModel,navHostController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleAuthUIClient = GoogleAuthUIClient(context,viewModel)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val signInText = buildAnnotatedString {
        append("Does Not Have An Account?")
        withStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(" Register")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = background)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    )
    {
        Image(
            painter = painterResource(id = R.drawable.stud_phone),
            contentDescription = null
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            placeholder = { Text(text = "Enter your email", color = textColor.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Email, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            placeholder = {
                Text(
                    text = "Enter your Password",
                    color = textColor.copy(alpha = 0.5f)
                )
            },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Lock, contentDescription = null) },
            trailingIcon = {
                Icon(
                    painter = painterResource(if (!showPassword) R.drawable.visibility_24dp_e3e3e3_fill0_wght400_grad0_opsz24__1_ else R.drawable.visibility_off_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                    contentDescription = null,
                    modifier = Modifier.clickable { showPassword = !showPassword })
            },
            supportingText = if (password.length > 8) {
                {
                    Text(
                        text = "At least 4 characters and at most 8 characters",
                        color = textColor.copy(alpha = 0.5f)
                    )
                }
            } else null,
            singleLine = true,
            isError = password.length > 8,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Button(
            onClick = {
                googleAuthUIClient.loginWithFirebase(
                    email = email,
                    password = password,
                    onSuccess = {
                        viewModel.setUserId(email)
                        navHostController.navigate(homeScreen)
                    },
                    onError = {
                        Toast.makeText(context,"Can't Sign In",Toast.LENGTH_LONG).show()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 20.dp,
                pressedElevation = 10.dp
            )
        ) {
            Text(
                text = "Sign In",
                fontSize = 18.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = textColor.copy(alpha = 0.5f),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 1.dp.toPx()
                    )
                }

        ) {
            Text(
                text = "Or",
                modifier = Modifier
                    .background(background)
                    .width(30.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                color = textColor.copy(alpha = 0.5f),
                fontStyle = FontStyle.Italic
            )
        }

        OutlinedButton(
            onClick = {
                scope.launch {
                    googleAuthUIClient.signIn()?.let{
                        viewModel.setUserId(it.userId!!)
                        navHostController.navigate(homeScreen)
                    } ?: Toast.makeText(context,"Can't Sign In ",Toast.LENGTH_LONG).show()
                } },
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(45.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_logo_foreground ),
                contentDescription = null,
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
            )
            Text(
                text = "Sign in with Google",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

        }

        Text(
            text = signInText,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navHostController.navigate(registerScreen)
                },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(200.dp).fillMaxWidth())


    }
}

@Composable
fun ProfileScreen(modifier:Modifier,viewModel: MainViewModel,navHostController: NavHostController){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firestoreFunction = FirestoreFunction(viewModel)
    var profileName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("Let's Chat") }
    val profilePic by remember { mutableIntStateOf(R.drawable.stud_phone) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = background)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Box(modifier = Modifier){
            Image(
                painter = painterResource(id = profilePic),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .border(2.dp,Color.LightGray,CircleShape)
                    .clip(CircleShape)
            )
            IconButton(onClick = {},
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .graphicsLayer(
                        translationX = 35f,
                        translationY = 15f
                    )

            ){
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = null,
                    modifier = Modifier
                        .background(Color.LightGray,CircleShape)
                        .size(25.dp)
                        .padding(3.dp)
                )
            }
        }


        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            value = profileName,
            onValueChange = { profileName = it },
            label = { Text(text = "Profile Name", fontSize = 17.sp, fontWeight = FontWeight.SemiBold) },
            placeholder = { Text(text = "Enter your Name", color = textColor.copy(alpha = 0.5f)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            value = bio,
            onValueChange = { bio = it },
            label = { Text(text = "Bio") },
            placeholder = { Text(text = "Enter your Bio", color = textColor.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Email, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )

        Button(
            onClick = {
                if(profileName.isEmpty() || bio.isEmpty()){
                    Toast.makeText(context,"Name and Bio cant be empty",Toast.LENGTH_LONG).show()

                }else{
                    scope.launch {
                        viewModel.getUserId()?.let{
                            firestoreFunction.updateProfileName(it,profileName)
                            firestoreFunction.updateBio(it,bio)
                            Toast.makeText(context,"Profile Updated",Toast.LENGTH_SHORT).show()
                            navHostController.navigate(homeScreen)
                        } ?: run{
                            Toast.makeText(context,"Can`t Update Your Profile please retry",Toast.LENGTH_LONG).show()
                        }
                    }

                }

            },
            modifier = Modifier
                .fillMaxWidth(0.35f)
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 20.dp,
                pressedElevation = 10.dp
            )
        ) {
            Text(
                text = "Get Started",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
        
    }

}





@Preview(showBackground = true)
@Composable
fun PreviewScreen(){

}