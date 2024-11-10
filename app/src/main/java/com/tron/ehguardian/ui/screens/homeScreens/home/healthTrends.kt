package com.trontech.ehguardian.ui.screens.homeScreens.home


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.trontech.ehguardian.R
import com.trontech.ehguardian.ui.AppViewModelProvider
import com.trontech.ehguardian.ui.screens.homeScreens.HomeViewModel
import com.trontech.ehguardian.ui.screens.homeScreens.settings.ModalBottomHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTrends(
    onDismiss: () -> Unit,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
        val context = LocalContext.current
    val newsList by homeViewModel.newsLiveData.observeAsState(emptyList())
    val loading by homeViewModel.isLoading.observeAsState(false)
    LaunchedEffect(Unit) {
        homeViewModel.fetchHealthNews(context = context)
    }

        ModalBottomSheet(
            modifier = Modifier.heightIn(max = 700.dp, min = 700.dp),
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp
            ),
            dragHandle = {
                ModalBottomHeader(
                    headerText = "Latest Health Trends For You",
                    onDismiss = onDismiss
                )
            },
            onDismissRequest = onDismiss
        ) {
            if(loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 20.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
                else{

            if (newsList.isEmpty()) {

                Column (
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){

                    Image(
                        painter = painterResource(id = R.drawable.news),
                        contentDescription = "No data Image"
                    )
                    Text(
                        text = "Sorry no news available",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))




            }


          else{  LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(newsList) { newsItem -> // Use items function to display the list
                    NewsCard(
                        title = newsItem.title.toString(),
                        shortDescription = newsItem.shortDescription.toString(),
                        date = newsItem.date.toString(),
                        image = newsItem.topImage,
                        onItemClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("${newsItem.url}")
                            }
                            // Check if there's an app to handle the Intent

                                context.startActivity(intent)


                        })



                }
            }
        }
    }
}}

@Composable
fun NewsCard(
    title: String,
    shortDescription: String,
    date: String ,
    image: String? = null,
    onItemClick: () -> Unit,// Allow image to be nullable
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(
            onClick = {
                onItemClick()
            }
        ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
        Card(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(height = 100.dp, width = 100.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {

            image?.let {
                AsyncImage(
                    contentScale = ContentScale.FillBounds,
                    model = image,
                    contentDescription = "News Image",
                    filterQuality = FilterQuality.Medium

                    )

            }
        }
    }
}
