/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.devbyteviewer

import android.app.Application
import android.os.Build
import androidx.work.*
import com.example.android.devbyteviewer.work.RefreshDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via WorkManager
 */
class DevByteApplication : Application() {

    // DONE (01) Create CoroutineScope variable applicationScope, using Dispatchers.Default.
    val applicationScope = CoroutineScope(Dispatchers.Default)

    // DONE (02) Create a delayedInit() function that calls setupRecurringWork() in
    // the coroutine you defined above.
    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()
    }

    // DONE (04) Create a setupRecurringWork() function and use a Builder to define a
    // repeatingRequest variable to handle scheduling work.
    fun setupRecurringWork() {
        // DONE (07) In setupRecurringWork(), define constraints to prevent work from occurring when
        // there is no network access or the device is low on battery.
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setRequiresDeviceIdle(true)
                    }
                }.build()

        // DONE (08) Add the constraints to the repeatingRequest definition.
        val repeastingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        // DONE (05) In setupRecurringWork(), get an instance of WorkManager and
        // launch call enqueuPeriodicWork() to schedule the work.
        WorkManager.getInstance().enqueueUniquePeriodicWork(
                RefreshDataWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeastingRequest)


    }

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        // DONE (03) Call delayedInit().
        delayedInit()
    }
}
