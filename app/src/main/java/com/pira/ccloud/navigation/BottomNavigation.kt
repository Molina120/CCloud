package com.pira.ccloud.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pira.ccloud.ui.glass.glass

/**
 * Floating Liquid Glass bottom bar: a single frosted pill that hovers over
 * the content (see MainActivity, which pads it 20dp/16dp from the edges)
 * instead of a full-width opaque bar. The selected tab gets a violet->cyan
 * gradient pill; the rest stay quiet glass so the active destination is
 * readable at a glance.
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 0.dp)
            .glass(RoundedCornerShape(32.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            AppScreens.screens.filter { it.showBottomBar }.forEach { screen ->
                val isSelected = currentRoute == screen.route
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1f,
                    animationSpec = tween(durationMillis = 200),
                    label = "scale"
                )
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else scheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 200),
                    label = "iconColor"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else scheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 200),
                    label = "labelColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .then(
                            if (isSelected) {
                                Modifier.background(
                                    Brush.horizontalGradient(
                                        listOf(scheme.primary, Color(0xFF7C5CFF))
                                    )
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                }
                            }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.scale(scale)
                    ) {
                        Icon(
                            imageVector = screen.icon ?: Icons.Default.Movie,
                            contentDescription = stringResource(screen.resourceId),
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(screen.resourceId),
                            color = labelColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
