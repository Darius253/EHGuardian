package com.example.ehguardian.ui.screens.homeScreens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.ehguardian.data.models.NewsItem
import com.example.ehguardian.ui.screens.homeScreens.ModalBottomHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTrends(
    onDismiss: () -> Unit,
    newsList: List<NewsItem> // Changed from NewsItem to List<NewsItem>
) {
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
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            items(newsList) { newsItem -> // Use items function to display the list
                NewsCard(
                    title = newsItem.title,
                    shortDescription = newsItem.shortDescription,
                    date = newsItem.date,
                    image = newsItem.topImage
                )
            }
        }
    }
}

@Composable
fun NewsCard(
    title: String,
    shortDescription: String,
    date: String ,
    image: String? = null // Allow image to be nullable
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
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
            // Load image using Coil or other image loading library
            image?.let {
                AsyncImage(
                    model = image,
                    contentDescription = "News Image",

                    )

            }
        }
    }
}
