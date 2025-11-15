package com.example.letschat.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.letschat.R
import com.example.letschat.loginScreen
import com.example.letschat.ui.theme.background
import com.example.letschat.ui.theme.textColor

@Composable
fun OnboardingScreen(modifier: Modifier,navController: NavHostController){
    val pagerState = rememberPagerState(pageCount = { 3 })
    Column(modifier = Modifier
        .fillMaxSize()
        .then(modifier)
    ){
        HorizontalPager(state = pagerState){ page ->
            OnboardingPageUi(page = page+1,navController)
        }
    }


}

@Composable
fun OnboardingPageUi(page:Int,navHostController: NavHostController){
    val imageId by remember{
        derivedStateOf {
            when(page){
                1 -> R.drawable.oi
                2 -> R.drawable.anaxious_phone
                else -> R.drawable.happy_cell
            }
        }
    }
    val titleId by remember {
        derivedStateOf {
            when(page){
                1 -> R.string.onboarding_title_intro
                2 -> R.string.onboarding_title_connection
                else -> R.string.onboarding_title_auth
            }

        }
    }
    val descriptionId by remember {
        derivedStateOf {
            when(page){
                1 -> R.string.onboarding_desc_intro
                2 -> R.string.onboarding_desc_connection
                else -> R.string.onboarding_desc_auth
            }
        }
    }
    Column(modifier = Modifier
            .fillMaxSize()
            .background(color = background),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.15f))
        Image(painter = painterResource(imageId),
            contentDescription = null,
            modifier = Modifier
                .size(250.dp)
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.15f))
        Text(text = stringResource(titleId),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = textColor,
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
        Text(text = stringResource(descriptionId),
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic,
            letterSpacing = 1.sp,
            color = textColor.copy(alpha = 0.7f),
            modifier = Modifier
                .padding(horizontal = 18.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f))
        if(page == 3){
            Button(onClick = {
                navHostController.navigate(loginScreen)
            },
                modifier = Modifier
                    .fillMaxWidth(0.65f),
                shape = RoundedCornerShape(10.dp)
                ){
                    Text(text = "Get Started",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
            }
        }
    }
}



