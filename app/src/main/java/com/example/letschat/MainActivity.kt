package com.example.letschat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.letschat.screens.HomeScreen
import com.example.letschat.screens.HomeScreenWithSideBar
import com.example.letschat.screens.LoginScreen
import com.example.letschat.screens.MassagesScreen
import com.example.letschat.screens.OnboardingScreen
import com.example.letschat.screens.ProfileScreen
import com.example.letschat.screens.RegisterScreen
import com.example.letschat.ui.theme.LetsChatTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase
    private lateinit var viewModel: MainViewModel
    private val auth by lazy { FirebaseAuth.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getDatabase(applicationContext)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(db.taskDao())
        )[MainViewModel::class.java]
        scheduleMessageCleanup(this)
        enableEdgeToEdge()
        if(auth.currentUser != null){
            Log.e("MainActivity","User is not null ${auth.uid} and ${auth.currentUser?.email}")
            viewModel.setUserId(auth.currentUser?.email ?: "")
        }
        setContent {
            LetsChatTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyNavGraph(viewModel = viewModel, modifier  = Modifier
                        .padding(top = innerPadding.calculateTopPadding())
                        .imePadding()
                        .navigationBarsPadding(),
                        startDest = if(auth.currentUser != null) homeScreen else onboarding)


                }
            }
        }
    }
}

class MainViewModelFactory(private val taskDao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(taskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

const val onboarding = "OnboardingScreen"
const val registerScreen = "RegisterScreen"
const val loginScreen = "LoginScreen"
const val profileScreen = "ProfileScreen"
const val homeScreen = "HomeScreen"
const val msgScreen = "MessagingScreen"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MyNavGraph(navController: NavHostController = rememberNavController(), viewModel: MainViewModel, modifier: Modifier, startDest:String) {
    Log.d("NavGraph","Started")
    NavHost(
        navController,
        startDestination = startDest,
        enterTransition   = { zoomIn() },
        exitTransition    = { zoomOut() },
        popEnterTransition = { zoomIn() },
        popExitTransition  = { zoomOut() }
    ) {
        composable(onboarding){ OnboardingScreen(modifier = modifier,navController) }
        composable(loginScreen){ LoginScreen(modifier,viewModel,navController) }
        composable(registerScreen){ RegisterScreen(modifier,viewModel,navController) }
        composable(profileScreen){ ProfileScreen(modifier,viewModel,navController) }
        composable(homeScreen){ HomeScreenWithSideBar(modifier=modifier,viewModel = viewModel,navController = navController) }
        composable(msgScreen){ MassagesScreen(modifier,viewModel,navController) }
    }
}



@OptIn(ExperimentalAnimationApi::class)
private fun zoomIn(): EnterTransition =
    scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(durationMillis = 300)
    ) + fadeIn(animationSpec = tween(300))


@OptIn(ExperimentalAnimationApi::class)
private fun zoomOut(): ExitTransition =
    scaleOut(
        targetScale = 1.2f,
        animationSpec = tween(durationMillis = 300)
    ) + fadeOut(animationSpec = tween(300))


