package com.karkoszka.translateandroidlocalizationxmlwithsheet

import android.app.Application
import android.net.Uri
import android.provider.SyncStateContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.*
import com.karkoszka.translateandroidlocalizationxmlwithsheet.workers.XmlToCsvWorker

class SheetViewModel (application: Application) : AndroidViewModel(application) {

    internal var xmlUri: Uri? = null
    internal var outputUri: Uri? = null
    private val workManager by lazy { initWorkManger(application) }

    private fun initWorkManger(application: Application): WorkManager {
        // provide custom configuration
        val myConfig = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

        // initialize WorkManager
        WorkManager.initialize(application, myConfig)

        return WorkManager.getInstance(application)
    }

    internal val outputWorkInfos: LiveData<List<WorkInfo>>
    internal val progressWorkInfoItems: LiveData<List<WorkInfo>>

    init {
        // This transformation makes sure that whenever the current work Id changes the WorkInfo
        // the UI is listening to changes
        outputWorkInfos = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
        progressWorkInfoItems = workManager.getWorkInfosByTagLiveData(TAG_PROGRESS)
    }

    internal fun cancelWork() {
        workManager.cancelUniqueWork(XML_MANIPULATION_WORK_NAME)
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    /**
     * Setters
     */
    internal fun setXmlUri(uri: String?) {
        xmlUri = uriOrNull(uri)
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }

    fun applyConversion() {
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<XmlToCsvWorker>()
                .setInputData(workDataOf(KEY_XML_URI to xmlUri.toString()))
                .build()
        workManager.enqueue(uploadWorkRequest)
    }

}