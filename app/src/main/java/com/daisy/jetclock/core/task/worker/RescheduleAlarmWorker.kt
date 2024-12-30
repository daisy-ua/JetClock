package com.daisy.jetclock.core.task.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.daisy.jetclock.core.task.WorkRequestManager
import com.daisy.jetclock.domain.usecase.RescheduleAlarmsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

@HiltWorker
class RescheduleAlarmWorker @AssistedInject constructor(
    @Assisted private val rescheduleAlarmsUseCase: RescheduleAlarmsUseCase,
    @Assisted private val workRequestManager: WorkRequestManager,
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            rescheduleAlarmsUseCase()
            workRequestManager.cancelWorker(RESCHEDULE_ALARM_TAG)
            Result.success()
        } catch (e: CancellationException) {
            Result.failure()
        } catch (e: Exception) {
            Log.e("RescheduleAlarmWorker", "${e.message}")
            Result.failure()
        }
    }
}

const val RESCHEDULE_ALARM_TAG = "RESCHEDULE_ALARM_TAG"