package com.daisy.jetclock.core.task.worker.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.daisy.jetclock.core.task.WorkRequestManager
import com.daisy.jetclock.core.task.worker.RescheduleAlarmWorker
import com.daisy.jetclock.domain.usecase.RescheduleAlarmsUseCase
import javax.inject.Inject

class RescheduleAlarmWorkerFactory @Inject constructor(
    private val rescheduleAlarmsUseCase: RescheduleAlarmsUseCase,
    private val workRequestManager: WorkRequestManager,
) : AssistedWorkerFactory {

    override fun create(appContext: Context, workerParams: WorkerParameters): ListenableWorker {
        return RescheduleAlarmWorker(
            rescheduleAlarmsUseCase,
            workRequestManager,
            appContext,
            workerParams
        )
    }
}