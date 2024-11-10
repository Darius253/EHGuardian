package com.trontech.ehguardian.ui.screens.homeScreens

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigation(
    items: List<BottomNavItem>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .height(75.dp)

            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        items.forEachIndexed { index, item ->
            AddItem(
                item = item,
                isSelected = index == selectedItem,
                onClick = { onItemSelected(index) }
            )
        }
    }
}

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home : BottomNavItem(
        title = "Home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )

    data object Health : BottomNavItem(
        title = "Measure",
        icon = Icons.Outlined.MonitorHeart,
        selectedIcon = Icons.Filled.MonitorHeart
    )

    data object History : BottomNavItem(
        title = "History",
        icon = Icons.Outlined.BarChart,
        selectedIcon = Icons.Filled.StackedBarChart
    )

    data object Profile : BottomNavItem(
        title = "Profile",
        icon = Icons.Outlined.AccountCircle,
        selectedIcon = Icons.Filled.Person
    )
}

@Composable
fun RowScope.AddItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        modifier = Modifier.padding(top = 26.dp, bottom = 26.dp),
        label = {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        },
        icon = {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.icon,
                contentDescription = item.title
            )
        },
        selected = isSelected,
        alwaysShowLabel = true,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            indicatorColor = MaterialTheme.colorScheme.surface
        )
    )
}
