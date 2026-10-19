package com.khatawat.diyn.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.khatawat.diyn.data.Debt
import com.khatawat.diyn.data.KhatawatDatabase
import java.util.concurrent.TimeUnit

class DebtReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val debtId = inputData.getLong(KEY_DEBT_ID, -1L)
        if (debtId == -1L) return Result.failure()

        val db = KhatawatDatabase.getInstance(applicationContext)
        val debt: Debt? = db.debtDao().getById(debtId)
        if (debt == null || debt.isPaid) return Result.success()

        val directionText = when (debt.direction) {
            com.khatawat.diyn.data.DebtDirection.OWED_TO_ME ->
                applicationContext.getString(com.khatawat.diyn.R.string.dir_owed_to_me)
            com.khatawat.diyn.data.DebtDirection.I_OWE ->
                applicationContext.getString(com.khatawat.diyn.R.string.dir_i_owe)
        }
        val title = applicationContext.getString(
            com.khatawat.diyn.R.string.reminder_title,
            debt.personName
        )
        val message = applicationContext.getString(
            com.khatawat.diyn.R.string.reminder_body,
            debt.amount.toString(),
            directionText,
            debt.description
        )
        NotificationHelper.showDebtReminder(applicationContext, debtId, title, message)
        return Result.success()
    }

    companion object {
        const val KEY_DEBT_ID = "debt_id"

        fun schedule(context: Context, debtId: Long, triggerAtMillis: Long) {
            val delay = (triggerAtMillis - System.currentTimeMillis()).coerceAtLeast(0L)
            val data = Data.Builder().putLong(KEY_DEBT_ID, debtId).build()
            val request = OneTimeWorkRequestBuilder<DebtReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("debt_$debtId")
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork("debt_reminder_$debtId", androidx.work.ExistingWorkPolicy.REPLACE, request)
        }
    }
}
