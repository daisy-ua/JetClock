package com.daisy.jetclock.core.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.daisy.jetclock.core.manager.AlarmController
import com.daisy.jetclock.core.manager.WorkRequestManager
import com.daisy.jetclock.repositories.AlarmRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

@HiltWorker
class RescheduleAlarmWorker @AssistedInject constructor(
    @Assisted private val alarmRepository: AlarmRepository,
    @Assisted private val alarmController: AlarmController,
    @Assisted private val workRequestManager: WorkRequestManager,
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            rescheduleAlarms()
            workRequestManager.cancelWorker(RESCHEDULE_ALARM_TAG)
            Result.success()
        } catch (e: CancellationException) {
            Result.failure()
        } catch (e: Exception) {
            Log.d("daisy-ua", "${e.message}")
            Result.failure()
        }
    }

    private suspend fun rescheduleAlarms() {
        val alarms = alarmRepository.getAllAlarms()
            .map { alarmList ->
                alarmList.filter { alarm -> alarm.isEnabled }
            }
            .firstOrNull { it.isNotEmpty() }

        alarms?.forEach { alarmController.schedule(it) }
    }
}

const val RESCHEDULE_ALARM_TAG = "RESCHEDULE_ALARM_TAG"