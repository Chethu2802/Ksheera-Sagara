package com.ksheera.sagara.ai

import com.ksheera.sagara.data.Expense
import com.ksheera.sagara.data.MilkIncome
import java.util.Calendar

/**
 * Real-time rule-based chatbot. Generates bilingual responses based on the
 * user's actual milk slips & expenses. No external API needed.
 *
 * Returns a list of message lines (each "EN  ·  KN") so the UI can show a
 * rich, multi-line answer per question.
 */
object Chatbot {

    private fun kn(en: String, kn: String) = "$en  ·  $kn"

    data class Reply(val lines: List<String>)

    val quickQuestions: List<Pair<String, String>> = listOf(
        "Today's profit" to "ಇಂದಿನ ಲಾಭ",
        "Best cow" to "ಅತ್ಯುತ್ತಮ ಹಸು",
        "Lowest cow" to "ಕಡಿಮೆ ಹಸು",
        "Total milk this week" to "ಈ ವಾರದ ಒಟ್ಟು ಹಾಲು",
        "Biggest expense" to "ದೊಡ್ಡ ಖರ್ಚು",
        "Health tips" to "ಆರೋಗ್ಯ ಸಲಹೆ",
        "Feed advice" to "ಮೇವು ಸಲಹೆ",
        "How to increase yield" to "ಇಳುವರಿ ಹೆಚ್ಚಿಸುವುದು ಹೇಗೆ"
    )

    fun reply(question: String, income: List<MilkIncome>, expenses: List<Expense>): Reply {
        val q = question.lowercase().trim()
        if (q.isBlank()) return Reply(listOf(kn("Ask me anything about your dairy.",
            "ನಿಮ್ಮ ಡೈರಿ ಬಗ್ಗೆ ಏನಾದರೂ ಕೇಳಿ.")))

        val now = Calendar.getInstance()
        val startOfDay = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val weekAgo = now.timeInMillis - 7L * 24 * 3600 * 1000

        val totalIncome = income.sumOf { it.total }
        val totalExp = expenses.sumOf { it.amount }
        val net = totalIncome - totalExp

        // ── Profit / today ──────────────────────────────────────────
        if (q.contains("profit") || q.contains("ಲಾಭ") || q.contains("today")) {
            val todayInc = income.filter { it.dateMillis >= startOfDay }.sumOf { it.total }
            val todayExp = expenses.filter { it.dateMillis >= startOfDay }.sumOf { it.amount }
            val todayNet = todayInc - todayExp
            return Reply(listOf(
                kn("Today's income: ₹${"%.0f".format(todayInc)}",
                    "ಇಂದಿನ ಆದಾಯ: ₹${"%.0f".format(todayInc)}"),
                kn("Today's expense: ₹${"%.0f".format(todayExp)}",
                    "ಇಂದಿನ ಖರ್ಚು: ₹${"%.0f".format(todayExp)}"),
                kn("Today's net: ₹${"%.0f".format(todayNet)}",
                    "ಇಂದಿನ ನಿವ್ವಳ: ₹${"%.0f".format(todayNet)}"),
                kn("Overall net so far: ₹${"%.0f".format(net)}",
                    "ಇದುವರೆಗಿನ ಒಟ್ಟು ನಿವ್ವಳ: ₹${"%.0f".format(net)}")
            ))
        }

        // ── Best / top cow ──────────────────────────────────────────
        if (q.contains("best") || q.contains("top") || q.contains("ಅತ್ಯುತ್ತಮ")) {
            if (income.isEmpty()) return Reply(listOf(kn("No milk slips yet.", "ಇನ್ನೂ ಹಾಲಿನ ಲೆಕ್ಕವಿಲ್ಲ.")))
            val byCow = income.groupBy { it.cowName }.mapValues { it.value.sumOf { s -> s.liters } }
            val top = byCow.maxByOrNull { it.value }!!
            return Reply(listOf(
                kn("Top cow: ${top.key} — ${"%.1f".format(top.value)} L total.",
                    "ಅತ್ಯುತ್ತಮ ಹಸು: ${top.key} — ಒಟ್ಟು ${"%.1f".format(top.value)} ಲೀ."),
                kn("Keep her diet & milking routine consistent.",
                    "ಅವಳ ಆಹಾರ ಮತ್ತು ಹಾಲು ಕರೆಯುವ ಸಮಯ ಸ್ಥಿರವಾಗಿರಲಿ.")
            ))
        }

        // ── Lowest cow ──────────────────────────────────────────────
        if (q.contains("low") || q.contains("worst") || q.contains("ಕಡಿಮೆ")) {
            if (income.isEmpty()) return Reply(listOf(kn("No data yet.", "ಇನ್ನೂ ಮಾಹಿತಿ ಇಲ್ಲ.")))
            val byCow = income.groupBy { it.cowName }.mapValues { it.value.sumOf { s -> s.liters } }
            val low = byCow.minByOrNull { it.value }!!
            return Reply(listOf(
                kn("Lowest yielding: ${low.key} — ${"%.1f".format(low.value)} L.",
                    "ಕಡಿಮೆ ಇಳುವರಿ: ${low.key} — ${"%.1f".format(low.value)} ಲೀ."),
                kn("Check her health, water, and mineral mix.",
                    "ಆರೋಗ್ಯ, ನೀರು, ಖನಿಜ ಮಿಶ್ರಣ ಪರಿಶೀಲಿಸಿ.")
            ))
        }

        // ── Weekly milk ─────────────────────────────────────────────
        if (q.contains("week") || q.contains("ವಾರ") || q.contains("milk")) {
            val weekLitres = income.filter { it.dateMillis >= weekAgo }.sumOf { it.liters }
            val weekIncome = income.filter { it.dateMillis >= weekAgo }.sumOf { it.total }
            return Reply(listOf(
                kn("Last 7 days: ${"%.1f".format(weekLitres)} L produced.",
                    "ಕಳೆದ 7 ದಿನ: ${"%.1f".format(weekLitres)} ಲೀ ಉತ್ಪಾದನೆ."),
                kn("Weekly income: ₹${"%.0f".format(weekIncome)}.",
                    "ವಾರದ ಆದಾಯ: ₹${"%.0f".format(weekIncome)}."),
                kn("Daily average: ${"%.1f".format(weekLitres / 7.0)} L.",
                    "ದೈನಂದಿನ ಸರಾಸರಿ: ${"%.1f".format(weekLitres / 7.0)} ಲೀ.")
            ))
        }

        // ── Expense breakdown ───────────────────────────────────────
        if (q.contains("expense") || q.contains("cost") || q.contains("ಖರ್ಚು")) {
            if (expenses.isEmpty()) return Reply(listOf(kn("No expenses recorded.",
                "ಯಾವುದೇ ಖರ್ಚು ದಾಖಲಾಗಿಲ್ಲ.")))
            val byCat = expenses.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }
            val biggest = byCat.maxByOrNull { it.value }!!
            return Reply(listOf(
                kn("Biggest expense: ${biggest.key} — ₹${"%.0f".format(biggest.value)}.",
                    "ದೊಡ್ಡ ಖರ್ಚು: ${biggest.key} — ₹${"%.0f".format(biggest.value)}."),
                kn("Total expense: ₹${"%.0f".format(totalExp)}.",
                    "ಒಟ್ಟು ಖರ್ಚು: ₹${"%.0f".format(totalExp)}."),
                kn("Try bulk-buying fodder to reduce cost 10–15%.",
                    "ಮೇವನ್ನು ಬೃಹತ್ ಪ್ರಮಾಣದಲ್ಲಿ ಖರೀದಿಸಿ 10–15% ಉಳಿತಾಯ.")
            ))
        }

        // ── Health ──────────────────────────────────────────────────
        if (q.contains("health") || q.contains("vaccine") || q.contains("ಆರೋಗ್ಯ") || q.contains("disease")) {
            return Reply(listOf(
                kn("Vaccinate against FMD every 6 months.",
                    "ಪ್ರತಿ 6 ತಿಂಗಳಿಗೊಮ್ಮೆ ಎಫ್‌ಎಂಡಿ ಲಸಿಕೆ ನೀಡಿ."),
                kn("Deworm cows every 3 months.",
                    "ಪ್ರತಿ 3 ತಿಂಗಳಿಗೊಮ್ಮೆ ಜಂತುಹುಳ ಔಷಧಿ ನೀಡಿ."),
                kn("Watch for mastitis: swollen udder, blood/clots in milk.",
                    "ಮಾಸ್ಟೈಟಿಸ್: ಊದಿದ ಕೆಚ್ಚಲು, ಹಾಲಲ್ಲಿ ರಕ್ತ/ಗಡ್ಡೆ ಗಮನಿಸಿ."),
                kn("Clean shed daily; provide dry bedding.",
                    "ಶೆಡ್ ದಿನವೂ ಸ್ವಚ್ಛಗೊಳಿಸಿ; ಒಣ ಹಾಸಿಗೆ ನೀಡಿ.")
            ))
        }

        // ── Feed / fodder ───────────────────────────────────────────
        if (q.contains("feed") || q.contains("fodder") || q.contains("ಮೇವು")) {
            return Reply(listOf(
                kn("Give 1 kg concentrate per 2.5 L of milk produced.",
                    "ಪ್ರತಿ 2.5 ಲೀ ಹಾಲಿಗೆ 1 ಕೆಜಿ ಸಾಂದ್ರೀಕೃತ ಆಹಾರ."),
                kn("Mix green + dry fodder 60:40 for balanced rumen.",
                    "ಹಸಿರು + ಒಣ ಮೇವು 60:40 ಮಿಶ್ರಣ ಮಾಡಿ."),
                kn("Add 50 g mineral mixture daily for higher fat %.",
                    "ಹೆಚ್ಚಿನ ಕೊಬ್ಬು %ಗಾಗಿ ದಿನಕ್ಕೆ 50 ಗ್ರಾಂ ಖನಿಜ ಮಿಶ್ರಣ ನೀಡಿ.")
            ))
        }

        // ── Increase yield ──────────────────────────────────────────
        if (q.contains("yield") || q.contains("increase") || q.contains("more milk") || q.contains("ಇಳುವರಿ")) {
            return Reply(listOf(
                kn("Milk at fixed times twice a day.",
                    "ದಿನಕ್ಕೆ ಎರಡು ಬಾರಿ ನಿಗದಿತ ಸಮಯಕ್ಕೆ ಹಾಲು ಕರೆಯಿರಿ."),
                kn("Give clean water 4–5 times a day.",
                    "ದಿನಕ್ಕೆ 4–5 ಬಾರಿ ಶುದ್ಧ ನೀರು ನೀಡಿ."),
                kn("Provide green fodder + protein-rich concentrate.",
                    "ಹಸಿರು ಮೇವು ಮತ್ತು ಪ್ರೋಟೀನ್‌ಯುಕ್ತ ಸಾಂದ್ರೀಕೃತ ಆಹಾರ ನೀಡಿ."),
                kn("Reduce stress: shade, ventilation, gentle handling.",
                    "ಒತ್ತಡ ತಗ್ಗಿಸಿ: ನೆರಳು, ಗಾಳಿ, ಮೃದು ನಿರ್ವಹಣೆ.")
            ))
        }

        // ── Price / rate ────────────────────────────────────────────
        if (q.contains("price") || q.contains("rate") || q.contains("ಬೆಲೆ")) {
            val avgRate = if (income.isNotEmpty())
                income.sumOf { it.total } / income.sumOf { it.liters }.coerceAtLeast(0.001)
            else 0.0
            return Reply(listOf(
                kn("Your average rate: ₹${"%.2f".format(avgRate)}/L.",
                    "ನಿಮ್ಮ ಸರಾಸರಿ ಬೆಲೆ: ₹${"%.2f".format(avgRate)}/ಲೀ."),
                kn("Higher fat % usually fetches a better price.",
                    "ಹೆಚ್ಚಿನ ಕೊಬ್ಬು % ಸಾಮಾನ್ಯವಾಗಿ ಉತ್ತಮ ಬೆಲೆ ತರುತ್ತದೆ.")
            ))
        }

        // ── Fallback: use the suggestion engine ─────────────────────
        val fallback = SuggestionEngine.suggest(income, expenses).take(4)
        val intro = kn("Here are insights based on your data:",
            "ನಿಮ್ಮ ದತ್ತಾಂಶದ ಆಧಾರದಲ್ಲಿ ಒಳನೋಟಗಳು:")
        return Reply(listOf(intro) + fallback)
    }
}
