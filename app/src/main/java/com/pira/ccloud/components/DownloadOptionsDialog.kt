package com.pira.ccloud.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pira.ccloud.data.model.Source
import com.pira.ccloud.ui.glass.GlassActionRow
import com.pira.ccloud.ui.glass.GlassSheet

/**
 * Liquid Glass download & play sheet.
 *
 * Was a tall opaque AlertDialog with seven identical buttons. Now a floating
 * glass bottom sheet, grouped in two clear sections — Download first, then
 * Play — each row naming what it actually does (browser / ADM / VLC / …)
 * so the user picks a destination, not a mystery button.
 */
@Composable
fun DownloadOptionsDialog(
    source: Source,
    onDismiss: () -> Unit,
    onCopyLink: () -> Unit,
    onDownloadWithBrowser: () -> Unit,
    onDownloadWithADM: () -> Unit,
    onOpenInVLC: () -> Unit,
    onOpenInMXPlayer: () -> Unit,
    onOpenInKMPlayer: () -> Unit
) {
    GlassSheet(
        onDismiss = onDismiss,
        title = "Download & Play",
        subtitle = "Quality ${source.quality} · choose how to handle this video"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Download",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            GlassActionRow(
                icon = Icons.Default.ContentCopy,
                title = "Copy Link",
                subtitle = "Copy the video URL to the clipboard",
                onClick = { onCopyLink(); onDismiss() }
            )
            GlassActionRow(
                icon = Icons.Default.Language,
                title = "Download with Browser",
                subtitle = "Let the browser handle the download",
                onClick = { onDownloadWithBrowser(); onDismiss() }
            )
            GlassActionRow(
                icon = Icons.Default.Download,
                title = "Download with ADM",
                subtitle = "Open in Advanced Download Manager",
                onClick = { onDownloadWithADM(); onDismiss() }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Play in an external player",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            GlassActionRow(
                icon = Icons.Default.PlayArrow,
                title = "Open in VLC Player",
                subtitle = "Stream with VLC",
                onClick = { onOpenInVLC(); onDismiss() }
            )
            GlassActionRow(
                icon = Icons.Default.PlayArrow,
                title = "Open in MX Player",
                subtitle = "Stream with MX Player",
                onClick = { onOpenInMXPlayer(); onDismiss() }
            )
            GlassActionRow(
                icon = Icons.Default.PlayArrow,
                title = "Open in KM Player",
                subtitle = "Stream with KM Player",
                onClick = { onOpenInKMPlayer(); onDismiss() }
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
