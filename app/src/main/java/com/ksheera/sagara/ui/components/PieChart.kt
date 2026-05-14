package com.ksheera.sagara.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val palette = listOf(
    Color(0xFF10B981), // emerald
    Color(0xFFF59E0B), // amber
    Color(0xFFEF4444), // red
    Color(0xFF3B82F6), // blue
    Color(0xFF8B5CF6), // violet
    Color(0xFF14B8A6), // teal
    Color(0xFFEC4899), // pink
    Color(0xFF64748B)  // slate
)

@Composable
fun PieChart(data: Map<String, Double>, modifier: Modifier = Modifier) {
    val entries = data.entries.toList()
    val total = entries.sumOf { it.value }.takeIf { it > 0 } ?: 1.0

    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Donut chart with gaps + percentage labels
            Canvas(Modifier.size(180.dp)) {
                val w = size.width
                val h = size.height
                val pad = 14f
                val arcSize = Size(w - pad * 2, h - pad * 2)
                val topLeft = Offset(pad, pad)
                val gapDeg = if (entries.size > 1) 1.5f else 0f

                var start = -90f
                entries.forEachIndexed { i, e ->
                    val sweep = (e.value / total * 360.0).toFloat() - gapDeg
                    if (sweep > 0f) {
                        drawArc(
                            color = palette[i % palette.size],
                            startAngle = start,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = 44f)
                        )
                    }
                    start += sweep + gapDeg
                }

                // Center text: total
                val cx = w / 2
                val cy = h / 2
                drawContext.canvas.nativeCanvas.apply {
                    val paintTitle = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#0F172A")
                        textSize = 30f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    val paintSub = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#64748B")
                        textSize = 18f
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawText("₹${"%.0f".format(total)}", cx, cy + 4f, paintTitle)
                    drawText("Total", cx, cy + 26f, paintSub)
                }

                // Percentage labels on each slice
                var a = -90.0
                entries.forEach { e ->
                    val pct = e.value / total * 100.0
                    val sweep = e.value / total * 360.0
                    if (pct >= 6.0) {
                        val mid = Math.toRadians(a + sweep / 2.0)
                        val r = (arcSize.minDimension / 2.0) - 6.0
                        val px = cx + r * cos(mid)
                        val py = cy + r * sin(mid)
                        drawContext.canvas.nativeCanvas.drawText(
                            "${"%.0f".format(pct)}%",
                            px.toFloat(), (py + 6).toFloat(),
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.WHITE
                                textSize = 22f
                                isAntiAlias = true
                                textAlign = android.graphics.Paint.Align.CENTER
                                isFakeBoldText = true
                            }
                        )
                    }
                    a += sweep
                }
            }

            Spacer(Modifier.width(14.dp))

            // Legend with values + percentages
            Column(Modifier.weight(1f)) {
                entries.forEachIndexed { i, e ->
                    val pct = e.value / total * 100.0
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 3.dp)
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                        ) {
                            Canvas(Modifier.matchParentSize()) {
                                drawRect(palette[i % palette.size])
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(e.key, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "₹${"%.0f".format(e.value)}  •  ${"%.1f".format(pct)}%",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
