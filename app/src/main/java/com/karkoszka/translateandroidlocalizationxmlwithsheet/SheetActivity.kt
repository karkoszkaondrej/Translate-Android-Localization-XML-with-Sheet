package com.karkoszka.translateandroidlocalizationxmlwithsheet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import com.karkoszka.translateandroidlocalizationxmlwithsheet.databinding.ActivitySheetBinding
import java.io.*


class SheetActivity : AppCompatActivity() {

    private lateinit var viewModel: SheetViewModel
    private lateinit var binding: ActivitySheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the ViewModel
        viewModel = ViewModelProvider(this)[SheetViewModel::class.java]

        // Xml uri should be stored in the ViewModel; put it there then display
        val xmlUriExtra = intent.getStringExtra(KEY_XML_URI)
        viewModel.setXmlUri(xmlUriExtra)
        viewModel.xmlUri?.let { xmlUri ->
           binding.editText.setText(readTextFromUri(xmlUri))
        }

        binding.goButton.setOnClickListener { viewModel.applyConversion() }

        // Setup view output image file button
        binding.seeFileButton.setOnClickListener {
            viewModel.outputUri?.let { currentUri ->
                val actionView = Intent(Intent.ACTION_VIEW, currentUri)
                actionView.resolveActivity(packageManager)?.run {
                    startActivity(actionView)
                }
            }
        }

        // Hookup the Cancel button
        binding.cancelButton.setOnClickListener { viewModel.cancelWork() }

        viewModel.outputWorkInfos.observe(this, workInfosObserver())

        // Show work progress
        viewModel.progressWorkInfoItems.observe(this, progressObserver())
    }

    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun progressObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            listOfWorkInfo.forEach { workInfo ->
                if (WorkInfo.State.RUNNING == workInfo.state) {
                    val progress = workInfo.progress.getInt(PROGRESS, 0)
                    binding.progressBar.progress = progress
                }
            }

        }
    }

    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workInfo = listOfWorkInfo[0]

            if (workInfo.state.isFinished) {
                showWorkFinished()

                // Normally this processing, which is not directly related to drawing views on
                // screen would be in the ViewModel. For simplicity we are keeping it here.
                val outputImageUri = workInfo.outputData.getString(KEY_XML_URI)

                // If there is an output file show "See File" button
                if (!outputImageUri.isNullOrEmpty()) {
                    viewModel.setOutputUri(outputImageUri)
                    binding.seeFileButton.visibility = View.VISIBLE
                }
            } else {
                showWorkInProgress()
            }
        }
    }

    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            goButton.visibility = View.GONE
            seeFileButton.visibility = View.GONE
        }
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        with(binding) {
            progressBar.visibility = View.GONE
            cancelButton.visibility = View.GONE
            goButton.visibility = View.VISIBLE
            progressBar.progress = 0
        }
    }
}