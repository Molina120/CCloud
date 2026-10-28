package com.pira.ccloud.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pira.ccloud.VideoPlayerActivity
import com.pira.ccloud.components.DownloadOptionsDialog
import com.pira.ccloud.components.ExpandableText
import com.pira.ccloud.ui.glass.GlassActionRow
import com.pira.ccloud.ui.glass.GlassIconButton
import com.pira.ccloud.ui.glass.GlassPrimaryButton
import com.pira.ccloud.ui.glass.GlassSectionHeader
import com.pira.ccloud.ui.glass.GlassSheet
import com.pira.ccloud.ui.glass.GlassSurface
import com.pira.ccloud.ui.glass.GlassTag
import com.pira.ccloud.data.model.FavoriteItem
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Source
import com.pira.ccloud.utils.DownloadUtils
import com.pira.ccloud.utils.StorageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleMovieScreen(
    movieId: Int,
    navController: NavController
) {
    var movie by remember { mutableStateOf<Movie?>(null) }
    val context = LocalContext.current
    
    LaunchedEffect(movieId) {
        movie = StorageUtils.loadMovieFromFile(context, movieId)
    }
    
    // Directly render content without Scaffold since it's already in MainScreen's Scaffold
    if (movie != null) {
        MovieDetailsContent(
            movie = movie!!,
            onBackClick = { navController.popBackStack() },
            onPlayClick = { source ->
                // Launch video player activity
                VideoPlayerActivity.start(context, source.url)
            },
            // Remove any padding from parent Scaffold to use full screen
            modifier = Modifier.fillMaxSize()
        )
    } else {
        // Show loading or error state
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Movie not found",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun MovieDetailsContent(
    movie: Movie,
    onBackClick: () -> Unit,
    onPlayClick: (Source) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val layoutDirection = LocalLayoutDirection.current
    var selectedSource by remember { mutableStateOf<Source?>(null) }
    var showSourceDialog by remember { mutableStateOf(false) }
    
    // Source selection dialog
    if (showSourceDialog && selectedSource != null) {
        SourceOptionsDialog(
            source = selectedSource!!,
            onDismiss = { showSourceDialog = false },
            onDownload = { source ->
                showSourceDialog = false
                DownloadUtils.openUrl(context, source.url)
            },
            onPlay = { source ->
                showSourceDialog = false
                onPlayClick(source)
            }
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Movie header with background cover and foreground image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
        ) {
            // Background cover image (blurred)
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(movie.cover)
                        .crossfade(true)
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(28.dp)
                    .scale(1.15f)
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )
            
            // Gradient overlay for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )
            
            // Row to contain poster and movie details side by side
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Foreground movie poster
                var showImageDialog by remember { mutableStateOf(false) }
                
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(movie.image)
                            .crossfade(true)
                            .build()
                    ),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                        .clickable { showImageDialog = true },
                    contentScale = ContentScale.Fit
                )
                
                // Image URL dialog
                if (showImageDialog) {
                    AlertDialog(
                        onDismissRequest = { showImageDialog = false },
                        title = { Text("Image Options") },
                        text = { Text("Choose an action for this image") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    DownloadUtils.copyToClipboard(context, movie.image)
                                    showImageDialog = false
                                }
                            ) {
                                Text("Copy Image URL")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showImageDialog = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
                
                // Movie details to the right of the poster
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    // Movie title
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Country and year
                    val countryText = if (movie.country.isNotEmpty()) {
                        "${movie.country.joinToString(", ") { it.title }} (${movie.year})"
                    } else {
                        "(${movie.year})"
                    }
                    
                    Text(
                        text = countryText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color.Yellow,
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = String.format("%.1f", movie.imdb),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            // Back button
            GlassIconButton(
                onClick = onBackClick,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            )
            
            // Favorite button
            var isFavorite by remember { mutableStateOf(false) }
            val context = LocalContext.current
            val movieId = movie.id
            
            // Check if movie is already favorite
            LaunchedEffect(movieId) {
                isFavorite = StorageUtils.isFavorite(context, movieId, "movie")
            }
            
            var showRemoveFavoriteDialog by remember { mutableStateOf(false) }
            
            // Confirmation dialog for removing from favorites
            if (showRemoveFavoriteDialog) {
                AlertDialog(
                    onDismissRequest = { showRemoveFavoriteDialog = false },
                    title = { Text("Remove from Favorites") },
                    text = { Text("Are you sure you want to remove this movie from your favorites?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                StorageUtils.removeFavorite(context, movieId, "movie")
                                isFavorite = false
                                showRemoveFavoriteDialog = false
                                // Show toast
                                android.widget.Toast.makeText(context, "Removed from favorites", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Remove")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showRemoveFavoriteDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
            
            // Frosted favourite button (logic unchanged, glass look only).
            androidx.compose.material3.IconButton(
                onClick = {
                    if (isFavorite) {
                        // Show confirmation dialog instead of directly removing
                        showRemoveFavoriteDialog = true
                    } else {
                        // Convert movie to favorite item with sources
                        val favoriteItem = FavoriteItem(
                            id = movie.id,
                            type = "movie",
                            title = movie.title,
                            description = movie.description,
                            year = movie.year,
                            imdb = movie.imdb,
                            rating = movie.rating,
                            duration = movie.duration,
                            image = movie.image,
                            cover = movie.cover,
                            genres = movie.genres,
                            country = movie.country,
                            sources = movie.sources // Include sources in favorites
                        )
                        StorageUtils.saveFavorite(context, favoriteItem)
                        isFavorite = true
                        // Show toast
                        android.widget.Toast.makeText(context, "Added to favorites", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFFF5C8A) else Color.White
                )
            }
        }
        
        // Genres
        if (movie.genres.isNotEmpty()) {
            GlassSectionHeader(
                title = "Genres",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
            )
            
            // Improved genres display with better wrapping and styling
            LazyRow(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movie.genres) { genre ->
                    GlassTag(text = genre.title)
                }
            }
        }
        
        // Description
        GlassSectionHeader(
            title = "Description",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )
        
        // Set layout direction to RTL for the description text
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ExpandableText(
                text = movie.description,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            )
        }
        
        // Sources/Quality options
        if (movie.sources.isNotEmpty()) {
            GlassSectionHeader(
                title = "Available Qualities",
                subtitle = "${movie.sources.size} options · tap one to play or download",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                movie.sources.forEach { source ->
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        onClick = {
                            selectedSource = source
                            showSourceDialog = true
                        },
                        contentPadding = 16.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = source.quality,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Play or download in this quality",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primary,
                                                Color(0xFF22D3EE)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SourceOptionsDialog(
    source: Source,
    onDismiss: () -> Unit,
    onDownload: (Source) -> Unit,
    onPlay: (Source) -> Unit
) {
    val context = LocalContext.current
    var showDownloadOptions by remember { mutableStateOf(false) }

    if (showDownloadOptions) {
        DownloadOptionsDialog(
            source = source,
            onDismiss = { showDownloadOptions = false },
            onCopyLink = { DownloadUtils.copyToClipboard(context, source.url) },
            onDownloadWithBrowser = { DownloadUtils.openUrl(context, source.url) },
            onDownloadWithADM = { DownloadUtils.openWithADM(context, source.url) },
            onOpenInVLC = { DownloadUtils.openWithVLC(context, source.url) },
            onOpenInMXPlayer = { DownloadUtils.openWithMXPlayer(context, source.url) },
            onOpenInKMPlayer = { DownloadUtils.openWithKMPlayer(context, source.url) }
        )
    }

    GlassSheet(
        onDismiss = onDismiss,
        title = source.quality,
        subtitle = "Choose an action for this video quality"
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GlassPrimaryButton(
                text = "Play in CCloud",
                icon = Icons.Default.PlayArrow,
                onClick = { onPlay(source) }
            )
            GlassActionRow(
                icon = Icons.Default.Download,
                title = "Download Options",
                subtitle = "Browser, ADM, VLC, MX Player, KM Player",
                onClick = { showDownloadOptions = true }
            )
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}

fun openUrl(context: Context, url: String) {
    DownloadUtils.openUrl(context, url)
}

