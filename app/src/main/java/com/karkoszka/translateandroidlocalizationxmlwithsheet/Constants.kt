@file:JvmName("Constants")

package com.karkoszka.translateandroidlocalizationxmlwithsheet

// Notification Channel constants

// Name of Notification Channel for verbose notifications of background work
@JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
@JvmField val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1

// The name of the image manipulation work
const val XML_MANIPULATION_WORK_NAME = "xml_manipulation_work"

// Other keys
const val OUTPUT_PATH = "translate_filter_outputs"
const val KEY_XML_URI = "KEY_XML_URI"
const val TAG_OUTPUT = "OUTPUT"

const val DELAY_TIME_MILLIS: Long = 300

// Progress Data Key
const val PROGRESS = "PROGRESS"
const val TAG_PROGRESS = "TAG_PROGRESS"