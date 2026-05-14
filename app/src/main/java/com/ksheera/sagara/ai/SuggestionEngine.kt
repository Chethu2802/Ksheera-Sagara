package com.ksheera.sagara.ai

import com.ksheera.sagara.data.Expense
import com.ksheera.sagara.data.MilkIncome
import java.util.Calendar
import kotlin.math.abs

/**
 * Real-time suggestion engine. Returns bilingual tips (English + Kannada)
 * based on the farmer's actual milk slips and expense history.
 *
 * Each line uses the format:  "<EN sentence>  ·  <Kannada sentence>"
 * UI just renders it as-is.
 *
 * Always returns at least 6 suggestions so the farmer gets a full picture.
 */
object SuggestionEngine {

    private fun kn(en: String, kn: String) = "$en  ·  $kn"

    fun suggest(income: List<MilkIncome>, expenses: List<Expense>): List<String> {
        val out = mutableListOf<String>()

        if (income.isEmpty() && expenses.isEmpty()) {
            return listOf(
                kn("Start by adding today's milk slip.", "ಇಂದಿನ ಹಾಲಿನ ಲೆಕ್ಕವನ್ನು ಸೇರಿಸಿ ಪ್ರಾರಂಭಿಸಿ."),
                kn("Add fodder, medical and labor expenses to track profit.",
                    "ಲಾಭ ಲೆಕ್ಕಕ್ಕೆ ಮೇವು, ವೈದ್ಯಕೀಯ, ಕೂಲಿ ಖರ್ಚುಗಳನ್ನು ಸೇರಿಸಿ."),
                kn("Record one slip per cow per day for accurate analytics.",
                    "ಪ್ರತಿ ಹಸುವಿನ ದಿನಕ್ಕೊಂದು ಲೆಕ್ಕ ದಾಖಲಿಸಿ."),
                kn("Healthy cow average: 8–12 L/day at 3.8–4.2% fat.",
                    "ಆರೋಗ್ಯವಂತ ಹಸು: ದಿನಕ್ಕೆ 8–12 ಲೀ, 3.8–4.2% ಕೊಬ್ಬು."),
                kn("Provide clean water 4–5 times a day to boost yield.",
                    "ಇಳುವರಿ ಹೆಚ್ಚಿಸಲು ದಿನಕ್ಕೆ 4–5 ಬಾರಿ ಶುದ್ಧ ನೀರು ಒದಗಿಸಿ."),
                kn("Vaccinate against FMD every 6 months.",
                    "ಪ್ರತಿ 6 ತಿಂಗಳಿಗೊಮ್ಮೆ ಎಫ್‌ಎಂಡಿ ಲಸಿಕೆ ನೀಡಿ.")
            )
        }

        // ── Profit / loss ───────────────────────────────────────────────
        val totalIncome = income.sumOf { it.total }
        val totalExp = expenses.sumOf { it.amount }
        val net = totalIncome - totalExp
        val margin = if (totalIncome > 0) net / totalIncome * 100 else 0.0
        out += if (net >= 0)
            kn("Net profit ₹${"%.0f".format(net)} (${"%.1f".format(margin)}% margin).",
                "ನಿವ್ವಳ ಲಾಭ ₹${"%.0f".format(net)} (${"%.1f".format(margin)}% ಮಾರ್ಜಿನ್).")
        else
            kn("Loss ₹${"%.0f".format(-net)}. Review fodder and medical costs.",
                "ನಷ್ಟ ₹${"%.0f".format(-net)}. ಮೇವು ಮತ್ತು ವೈದ್ಯಕೀಯ ಖರ್ಚು ಪರಿಶೀಲಿಸಿ.")

        // ── Fat % health ────────────────────────────────────────────────
        if (income.isNotEmpty()) {
            val avgFat = income.map { it.fatPercent }.average()
            out += when {
                avgFat < 3.5 -> kn(
                    "Average fat ${"%.2f".format(avgFat)}% is low — add cottonseed cake & mineral mixture.",
                    "ಸರಾಸರಿ ಕೊಬ್ಬು ${"%.2f".format(avgFat)}% ಕಡಿಮೆ — ಹತ್ತಿ ಬೀಜದ ಹಿಂಡಿ ಮತ್ತು ಖನಿಜ ಮಿಶ್ರಣ ಸೇರಿಸಿ."
                )
                avgFat > 4.8 -> kn(
                    "Fat ${"%.2f".format(avgFat)}% is excellent. Negotiate a higher per-litre price.",
                    "ಕೊಬ್ಬು ${"%.2f".format(avgFat)}% ಉತ್ತಮ. ಹೆಚ್ಚಿನ ದರಕ್ಕೆ ಮಾತುಕತೆ ಮಾಡಿ."
                )
                else -> kn(
                    "Fat ${"%.2f".format(avgFat)}% is healthy. Maintain current feed mix.",
                    "ಕೊಬ್ಬು ${"%.2f".format(avgFat)}% ಆರೋಗ್ಯಕರ. ಪ್ರಸ್ತುತ ಮೇವು ಮುಂದುವರಿಸಿ."
                )
            }
        }

        // ── 7-day yield trend ───────────────────────────────────────────
        if (income.isNotEmpty()) {
            val now = System.currentTimeMillis()
            val day = 24L * 3600 * 1000
            val last7 = income.filter { now - it.dateMillis <= 7 * day }.sumOf { it.liters }
            val prev7 = income.filter { now - it.dateMillis in (7 * day + 1)..(14 * day) }.sumOf { it.liters }
            if (prev7 > 0) {
                val change = (last7 - prev7) / prev7 * 100
                out += when {
                    change >= 5 -> kn(
                        "Milk yield up ${"%.1f".format(change)}% vs last week. Keep current routine.",
                        "ಕಳೆದ ವಾರಕ್ಕಿಂತ ಹಾಲು ${"%.1f".format(change)}% ಹೆಚ್ಚಾಗಿದೆ. ಪ್ರಸ್ತುತ ಕ್ರಮ ಮುಂದುವರಿಸಿ."
                    )
                    change <= -5 -> kn(
                        "Yield dropped ${"%.1f".format(abs(change))}% vs last week — check water, feed and heat stress.",
                        "ಕಳೆದ ವಾರಕ್ಕಿಂತ ಇಳುವರಿ ${"%.1f".format(abs(change))}% ಕಡಿಮೆ — ನೀರು, ಮೇವು, ಶಾಖ ಪರಿಶೀಲಿಸಿ."
                    )
                    else -> kn(
                        "Yield is stable (${"%.1f".format(last7)} L last 7 days).",
                        "ಇಳುವರಿ ಸ್ಥಿರವಾಗಿದೆ (ಕಳೆದ 7 ದಿನ ${"%.1f".format(last7)} ಲೀ)."
                    )
                }
            } else {
                out += kn(
                    "Last 7 days yield: ${"%.1f".format(last7)} L.",
                    "ಕಳೆದ 7 ದಿನಗಳ ಇಳುವರಿ: ${"%.1f".format(last7)} ಲೀ."
                )
            }
        }

        // ── Expense breakdown ───────────────────────────────────────────
        if (expenses.isNotEmpty()) {
            val byCat = expenses.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }
            val fodder = byCat["FODDER"] ?: 0.0
            val medical = byCat["MEDICAL"] ?: 0.0
            if (totalIncome > 0 && fodder > totalIncome * 0.6)
                out += kn(
                    "Fodder cost is ${"%.0f".format(fodder / totalIncome * 100)}% of income. Try cooperative bulk buying or silage.",
                    "ಮೇವು ಖರ್ಚು ಆದಾಯದ ${"%.0f".format(fodder / totalIncome * 100)}%. ಸಹಕಾರಿ ಖರೀದಿ ಅಥವಾ ಸೈಲೇಜ್ ಪ್ರಯತ್ನಿಸಿ."
                )
            else
                out += kn(
                    "Top expense: ${byCat.maxByOrNull { it.value }?.key ?: "—"} (₹${"%.0f".format(byCat.values.max())}).",
                    "ಅತಿ ಹೆಚ್ಚು ಖರ್ಚು: ${byCat.maxByOrNull { it.value }?.key ?: "—"} (₹${"%.0f".format(byCat.values.max())})."
                )
            if (totalIncome > 0 && medical > totalIncome * 0.15)
                out += kn(
                    "Medical cost is high — schedule a vet visit and deworm the herd.",
                    "ವೈದ್ಯಕೀಯ ಖರ್ಚು ಹೆಚ್ಚಿದೆ — ಪಶುವೈದ್ಯ ಭೇಟಿ ಮತ್ತು ಜಂತುಹುಳ ಔಷಧಿ ನೀಡಿ."
                )
        }

        // ── Per-cow performance ─────────────────────────────────────────
        val perCow = income.groupBy { it.cowName }.mapValues { it.value.sumOf { i -> i.liters } }
        if (perCow.size > 1) {
            val low = perCow.minByOrNull { it.value }!!
            val high = perCow.maxByOrNull { it.value }!!
            out += kn(
                "Top yielder: ${high.key} (${"%.1f".format(high.value)} L). Lowest: ${low.key} (${"%.1f".format(low.value)} L) — health check recommended.",
                "ಅತ್ಯುತ್ತಮ: ${high.key} (${"%.1f".format(high.value)} ಲೀ). ಕಡಿಮೆ: ${low.key} (${"%.1f".format(low.value)} ಲೀ) — ಆರೋಗ್ಯ ಪರೀಕ್ಷೆ ಮಾಡಿ."
            )
        } else if (perCow.size == 1) {
            val (c, l) = perCow.entries.first()
            out += kn(
                "$c total yield: ${"%.1f".format(l)} L. Add more cows to compare performance.",
                "$c ಒಟ್ಟು ಇಳುವರಿ: ${"%.1f".format(l)} ಲೀ. ಹೋಲಿಕೆಗೆ ಇನ್ನಷ್ಟು ಹಸುಗಳನ್ನು ಸೇರಿಸಿ."
            )
        }

        // ── Daily average ───────────────────────────────────────────────
        if (income.isNotEmpty()) {
            val days = income.map { dayKey(it.dateMillis) }.toSet().size.coerceAtLeast(1)
            val avgPerDay = income.sumOf { it.liters } / days
            out += kn(
                "Daily average: ${"%.1f".format(avgPerDay)} L across $days recorded day(s).",
                "ದೈನಂದಿನ ಸರಾಸರಿ: ${"%.1f".format(avgPerDay)} ಲೀ ($days ದಿನಗಳಲ್ಲಿ)."
            )
        }

        // ── Seasonal tip (always useful) ────────────────────────────────
        out += seasonalTip()

        // ── Always-on best practice ─────────────────────────────────────
        out += kn(
            "Tip: clean udders before milking and keep sheds dry to prevent mastitis.",
            "ಸಲಹೆ: ಹಾಲು ಕರೆಯುವ ಮೊದಲು ಕೆಚ್ಚಲು ಶುಚಿಮಾಡಿ ಮತ್ತು ಕೊಟ್ಟಿಗೆ ಒಣಗಿಡಿ — ಮ್ಯಾಸ್ಟೈಟಿಸ್ ತಡೆಯಲು."
        )

        return out
    }

    private fun dayKey(millis: Long): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        return c.timeInMillis
    }

    private fun seasonalTip(): String {
        val m = Calendar.getInstance().get(Calendar.MONTH) + 1
        return when (m) {
            in 3..5 -> kn(
                "Summer: provide shade, sprinklers and electrolyte water to avoid heat stress.",
                "ಬೇಸಿಗೆ: ನೆರಳು, ಸಿಂಪರಣೆ ಮತ್ತು ಎಲೆಕ್ಟ್ರೋಲೈಟ್ ನೀರು — ಶಾಖದಿಂದ ರಕ್ಷಿಸಿ."
            )
            in 6..9 -> kn(
                "Monsoon: keep sheds dry, deworm cows, and watch for foot rot.",
                "ಮಳೆಗಾಲ: ಕೊಟ್ಟಿಗೆ ಒಣಗಿಡಿ, ಜಂತುಹುಳ ಔಷಧಿ ನೀಡಿ, ಗೊರಸಿನ ಕೊಳೆತ ಗಮನಿಸಿ."
            )
            in 10..11 -> kn(
                "Post-monsoon: harvest green fodder and ensile for dry months.",
                "ಮಳೆಯ ನಂತರ: ಹಸಿರು ಮೇವು ಸಂಗ್ರಹಿಸಿ ಸೈಲೇಜ್ ತಯಾರಿಸಿ."
            )
            else -> kn(
                "Winter: feed warm water, add jaggery and increase concentrate slightly.",
                "ಚಳಿಗಾಲ: ಬಿಸಿನೀರು ಕೊಡಿ, ಬೆಲ್ಲ ಸೇರಿಸಿ, ಸಾಂದ್ರ ಆಹಾರ ಸ್ವಲ್ಪ ಹೆಚ್ಚಿಸಿ."
            )
        }
    }
}
