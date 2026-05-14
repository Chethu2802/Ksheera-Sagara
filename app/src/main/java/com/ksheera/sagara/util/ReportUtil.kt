package com.ksheera.sagara.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.ksheera.sagara.viewmodel.DashboardState
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ReportUtil {
    private val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun build(title: String, s: DashboardState): String = buildString {
        appendLine("KSHEERA SAGARA / ಕ್ಷೀರ ಸಾಗರ")
        appendLine(title); appendLine("=".repeat(40))
        appendLine("Total Income: ₹${"%.2f".format(s.totalIncome)}")
        appendLine("Total Expense: ₹${"%.2f".format(s.totalExpense)}")
        appendLine("Net: ₹${"%.2f".format(s.net)} (${if (s.net >= 0) "PROFIT" else "LOSS"})")
        appendLine(); appendLine("Cow-wise:")
        s.cows.forEach { appendLine(" - ${it.cow}: ${"%.1f".format(it.liters)}L  Fat ${"%.2f".format(it.avgFat)}%  ₹${"%.0f".format(it.income)}") }
        appendLine(); appendLine("Expense breakdown:")
        s.expenseByCategory.forEach { (k, v) -> appendLine(" - $k: ₹${"%.0f".format(v)}") }
        appendLine(); appendLine("Recent income entries:")
        s.incomeList.take(20).forEach { appendLine(" * ${df.format(Date(it.dateMillis))} ${it.cowName} ${it.liters}L @₹${it.pricePerLiter}") }
        appendLine(); appendLine("Recent expenses:")
        s.expenseList.take(20).forEach { appendLine(" * ${df.format(Date(it.dateMillis))} ${it.category} ₹${it.amount} ${it.note}") }
    }

    fun saveToFile(ctx: Context, content: String, name: String): File {
        val dir = File(ctx.filesDir, "reports").apply { mkdirs() }
        val f = File(dir, name)
        f.writeText(content)
        return f
    }

    fun share(ctx: Context, file: File, mime: String = "text/plain") {
        val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", file)
        val i = Intent(Intent.ACTION_SEND).apply {
            type = mime
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Ksheera Sagara Report")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(Intent.createChooser(i, "Share report").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Build a PDF from the report text, save into app files dir, and return the file. */
    fun buildPdf(ctx: Context, content: String, name: String): File {
        val doc = PdfDocument()
        val pageWidth = 595   // A4 @72dpi
        val pageHeight = 842
        val margin = 36f
        val titlePaint = Paint().apply { textSize = 16f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 11f }
        val lineHeight = 16f

        val lines = content.split("\n")
        var pageNum = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create())
        var canvas = page.canvas
        var y = margin + 20f
        canvas.drawText("Ksheera Sagara — Report", margin, y, titlePaint)
        y += lineHeight + 8f

        for (raw in lines) {
            // wrap long lines
            val maxChars = 95
            val chunks = if (raw.length <= maxChars) listOf(raw)
                else raw.chunked(maxChars)
            for (chunk in chunks) {
                if (y > pageHeight - margin) {
                    doc.finishPage(page)
                    pageNum++
                    page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create())
                    canvas = page.canvas
                    y = margin + 10f
                }
                canvas.drawText(chunk, margin, y, bodyPaint)
                y += lineHeight
            }
        }
        doc.finishPage(page)

        val dir = File(ctx.filesDir, "reports").apply { mkdirs() }
        val f = File(dir, name)
        FileOutputStream(f).use { doc.writeTo(it) }
        doc.close()
        return f
    }

    /**
     * Save the PDF to the public Downloads folder so the user can find it in their file manager.
     * Returns a human readable location string.
     */
    fun savePdfToDownloads(ctx: Context, pdfFile: File, displayName: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = ctx.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/KsheeraSagara")
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: error("Could not create download entry")
            resolver.openOutputStream(uri).use { out ->
                pdfFile.inputStream().use { it.copyTo(out!!) }
            }
            "Downloads/KsheeraSagara/$displayName"
        } else {
            val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val target = File(downloads, displayName)
            pdfFile.copyTo(target, overwrite = true)
            target.absolutePath
        }
    }

    /** Render the report text to a PNG bitmap and save it to app files dir. */
    fun buildImage(ctx: Context, content: String, name: String): File {
        val width = 1080
        val padding = 48f
        val titlePaint = Paint().apply {
            color = Color.BLACK; textSize = 38f; isFakeBoldText = true; isAntiAlias = true
        }
        val bodyPaint = Paint().apply {
            color = Color.DKGRAY; textSize = 26f; isAntiAlias = true
        }
        val lineHeight = 36f
        val maxChars = 60
        val lines = mutableListOf<String>()
        content.split("\n").forEach { raw ->
            if (raw.length <= maxChars) lines.add(raw) else lines.addAll(raw.chunked(maxChars))
        }
        val height = (padding * 2 + 60f + lines.size * lineHeight).toInt().coerceAtLeast(400)
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)
        canvas.drawText("Ksheera Sagara — Monthly Financial Summary", padding, padding + 30f, titlePaint)
        var y = padding + 30f + lineHeight + 16f
        for (l in lines) { canvas.drawText(l, padding, y, bodyPaint); y += lineHeight }

        val dir = File(ctx.filesDir, "reports").apply { mkdirs() }
        val f = File(dir, name)
        FileOutputStream(f).use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bmp.recycle()
        return f
    }

    /** Save image to public Pictures/KsheeraSagara folder. */
    fun saveImageToGallery(ctx: Context, imageFile: File, displayName: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = ctx.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/KsheeraSagara")
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: error("Could not create image entry")
            resolver.openOutputStream(uri).use { out ->
                imageFile.inputStream().use { it.copyTo(out!!) }
            }
            "Pictures/KsheeraSagara/$displayName"
        } else {
            val pics = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val target = File(pics, displayName)
            imageFile.copyTo(target, overwrite = true)
            target.absolutePath
        }
    }
}

