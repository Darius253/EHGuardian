package com.tron.ehguardian.ui.screens.onBoardingScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tron.ehguardian.R

@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onFinalScreenClick: () -> Unit
) {
    var currentItem by rememberSaveable { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ItemsIndicator(size = 4, currentItem = currentItem)
        }
        item {
            OnBoardingContent(
                modifier = modifier,
                currentItem = currentItem
            )
        }
        item {
            Spacer(modifier = Modifier.padding(80.dp))
            NavigationButtons(
                currentItem = currentItem,
                onNextClick = { if (currentItem < 3) currentItem += 1 },
                onSkipClick = {
                    currentItem = 3
                },
                onFinalPageClick = {onFinalScreenClick()}

            )
        }
    }
}

@Composable
fun ItemsIndicator(size: Int, currentItem: Int) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        items(size) { index ->
            HorizontalDivider(
                modifier = Modifier
                    .width(90.dp)
                    .padding(start = 5.dp),
                color = if (index == currentItem) MaterialTheme.colorScheme.errorContainer else Color.Black,
                thickness = 3.dp
            )
        }
    }
}

@Composable
fun OnBoardingContent(modifier: Modifier, currentItem: Int) {
    val onboardingItems = listOf(
        OnboardingData(
            image = R.drawable.social_work,
            title = "Welcome to ",
            description = "Your partner in managing your blood pressure for a healthier life. Let’s make health monitoring easy and empowering."
        ),
        OnboardingData(
            image = R.drawable.data_analyst,
            title = "Gain Meaningful Insights",
            description = "Visualize your progress and discover patterns in your blood pressure. Knowledge is power when it comes to your well-being."
        ),
        OnboardingData(
            image = R.drawable.reminder,
            title = "Stay On Top of Your Health",
            description = "Receive reminders to check and log your blood pressure. Never miss a beat in your health journey."
        ),
        OnboardingData(
            image = R.drawable.hospital,
            title = "Find Nearby Hospitals Instantly",
            description = "Need to find a hospital or medical center? Use our location-based feature to discover hospitals close to you, ensuring that help is always within reach when you need it most."
        )
    )

    onboardingItems.getOrNull(currentItem)?.let {
        OnBoardingItem(
            modifier = modifier,
            index = currentItem,
            image = it.image,
            title = it.title,
            description = it.description
        )
    }
}

@Composable
fun OnBoardingItem(
    modifier: Modifier = Modifier,
    index: Int,
    image: Int,
    title: String,
    description: String
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(90.dp))
        AnimatedContent(
            targetState = index,
            transitionSpec = {
                scaleIn(initialScale = 0.92f, animationSpec = tween(100, delayMillis = 90))  togetherWith
                        fadeOut(
                    animationSpec = tween(10)
                )
            }, label = "animated content"
        ) { targetCount ->
            Image(
                painter = painterResource(id = image),
                contentDescription = "Onboarding Item $targetCount",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

            Spacer(modifier = Modifier.height(20.dp))
            if (index == 0) {
                Row {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.W600
                    )
                    Text(
                        text = "EHGuardian",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.W600
                    )
                }
            } else {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.W600
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }


@Composable
fun NavigationButtons(
    currentItem: Int,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    onFinalPageClick: () ->Unit,
) {
    if (currentItem != 3) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Skip",
                modifier = Modifier
                    .clickable {
                        onSkipClick()
                    }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onNextClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Next")
            }
        }
    } else {
        Button(
            onClick = {onFinalPageClick()},
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Take the First Step")
        }
    }
}

data class OnboardingData(
    val image: Int,
    val title: String,
    val description: String
)
