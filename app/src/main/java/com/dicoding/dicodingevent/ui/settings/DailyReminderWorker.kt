package com.dicoding.dicodingevent.ui.settings

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.dicodingevent.BuildConfig
import com.dicoding.dicodingevent.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class DailyReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val notificationHelper = NotificationHelper()

    override fun doWork(): Result {
        CoroutineScope(Dispatchers.IO).launch {
            fetchAndNotifyEvent(applicationContext)
        }
        return Result.success()
    }

    private fun fetchAndNotifyEvent(context: Context) {
        val client = OkHttpClient()
        val notifUrl = BuildConfig.NOTIFICATION_URL
        val request = Request.Builder()
            .url(notifUrl)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                if (!responseData.isNullOrEmpty()) {
                    val json = JSONObject(responseData)

                    // Check if "events" array exists
                    if (json.has("events") && json.getJSONArray("events").length() > 0) {
                        val event = json.getJSONArray("events").getJSONObject(0)
                        val eventName = event.optString("name", "Event")
                        val eventTime = event.optString("beginTime", "No time specified")

                        notificationHelper.showNotification(context, eventName, "Starts at: $eventTime")
                    } else {
                        println("No events found in response.")
                    }
                } else {
                    println("Response data is empty or null.")
                }
            } else {
                println("Failed to fetch events: ${response.code}")
            }
        }
    }

}


object DailyReminderManager{
    fun startReminder(context: Context){
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelReminder(context: Context){
        WorkManager.getInstance(context).cancelAllWorkByTag("DailyReminder")
    }
}