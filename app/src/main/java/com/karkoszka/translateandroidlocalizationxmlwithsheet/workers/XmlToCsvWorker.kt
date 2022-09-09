package com.karkoszka.translateandroidlocalizationxmlwithsheet.workers

import android.content.Context
import android.provider.SyncStateContract
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.karkoszka.translateandroidlocalizationxmlwithsheet.KEY_XML_URI
import com.karkoszka.translateandroidlocalizationxmlwithsheet.OUTPUT_PATH
import timber.log.Timber
import java.io.File

class XmlToCsvWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        // Makes a notification when the work starts and slows down the work so that
        // it's easier to see each WorkRequest start, even on emulated devices
        val resourceUri = inputData.getString(KEY_XML_URI) ?:
        throw IllegalArgumentException("Invalid input uri")

        makeStatusNotification("Converting Xml to Csv \n $resourceUri", applicationContext)

        sleep()

        return try {
           Timber.i("Xml to Csv Worker running.")
            Result.success()
        } catch (exception: Exception) {
            Timber.e(exception)
            Result.failure()
        }
    }
}