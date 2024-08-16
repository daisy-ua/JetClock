package com.daisy.jetclock.core.worker.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.daisy.jetclock.core.manager.AlarmSchedulerManager
import com.daisy.jetclock.core.manager.WorkRequestManager
import com.daisy.jetclock.core.worker.RescheduleAlarmWorker
import com.daisy.jetclock.repositories.AlarmRepository
import javax.inject.Inject

class RescheduleAlarmWorkerFactory @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmSchedulerManager: AlarmSchedulerManager,
    private val workRequestManager: WorkRequestManager,
) : AssistedWorkerFactory {

    override fun create(appContext: Context, workerParams: WorkerParameters): ListenableWorker {
        return RescheduleAlarmWorker(
            alarmRepository,
            alarmSchedulerManager,
            workRequestManager,
            appContext,
            workerParams
        )
    }
}