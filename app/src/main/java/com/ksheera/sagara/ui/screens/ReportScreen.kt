package com.ksheera.sagara.ui.screens

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.ksheera.sagara.ui.components.ScrollFabsOverlay
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ksheera.sagara.util.ReportUtil
import com.ksheera.sagara.viewmodel.FarmViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportScreen(nav: NavHostController, farm: FarmViewModel) {

    val __scrState = rememberScrollState()
    val __scrScope = rememberCoroutineScope()
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {

    val ctx = LocalContext.current
    val cal = remember { Calendar.getInstance() }
    var year by remember { mutableStateOf(cal.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(cal.get(Calendar.MONTH)) }
    val months = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

    val s = remember(year, month, farm.incomes.collectAsState().value, farm.expenses.collectAsState().value) {
        farm.monthSummary(year, month)
    }
    val title = "Monthly Financial Summary — ${months[month]} $year"
    val text = remember(s) { ReportUtil.build(title, s) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Monthly Financial Summary / ಮಾಸಿಕ ಆರ್ಥಿಕ ಸಾರಾಂಶ") },
            navigationIcon = { IconButton({ nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } })
    }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().padding(16.dp)) {
            Row {
                OutlinedButton(onClick = { year-- }) { Text("◀") }
                Spacer(Modifier.width(8.dp))
                Text("$year", modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically))
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = { year++ }) { Text("▶") }
            }
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                months.forEachIndexed { i, m ->
                    FilterChip(selected = i == month, onClick = { month = i }, label = { Text(m) })
                }
            }
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val name = "ksheera_${year}_${month + 1}.txt"
                    val f = ReportUtil.saveToFile(ctx, text, name)
                    Toast.makeText(ctx, "Saved: ${f.absolutePath}", Toast.LENGTH_LONG).show()
                }) { Icon(Icons.Default.Download, null); Spacer(Modifier.width(6.dp)); Text("Download TXT") }

                Button(onClick = {
                    val pdfName = "ksheera_${year}_${month + 1}.pdf"
                    try {
                        val pdf = ReportUtil.buildPdf(ctx, text, pdfName)
                        val location = ReportUtil.savePdfToDownloads(ctx, pdf, pdfName)
                        Toast.makeText(ctx, "PDF saved to $location", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "PDF failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }) { Icon(Icons.Default.PictureAsPdf, null); Spacer(Modifier.width(6.dp)); Text("Export PDF") }

                OutlinedButton(onClick = {
                    val pdfName = "ksheera_${year}_${month + 1}.pdf"
                    val pdf = ReportUtil.buildPdf(ctx, text, pdfName)
                    ReportUtil.share(ctx, pdf, "application/pdf")
                }) { Icon(Icons.Default.Share, null); Spacer(Modifier.width(6.dp)); Text("Share PDF") }

                Button(onClick = {
                    val imgName = "ksheera_${year}_${month + 1}.png"
                    try {
                        val img = ReportUtil.buildImage(ctx, text, imgName)
                        val location = ReportUtil.saveImageToGallery(ctx, img, imgName)
                        Toast.makeText(ctx, "Image saved to $location", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Image failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }) { Icon(Icons.Default.Image, null); Spacer(Modifier.width(6.dp)); Text("Export Image") }

                OutlinedButton(onClick = {
                    val imgName = "ksheera_${year}_${month + 1}.png"
                    val img = ReportUtil.buildImage(ctx, text, imgName)
                    ReportUtil.share(ctx, img, "image/png")
                }) { Icon(Icons.Default.Share, null); Spacer(Modifier.width(6.dp)); Text("Share Image") }
            }
            Spacer(Modifier.height(12.dp))
            Card(Modifier.weight(1f).fillMaxWidth()) {
                Column(Modifier.verticalScroll(__scrState).padding(12.dp)) {
                    Text(text, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                }
            }
        }
    }

        ScrollFabsOverlay(__scrState, __scrScope, Modifier.align(Alignment.BottomEnd))
    }
}