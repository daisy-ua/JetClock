package com.daisy.jetclock.core.receiver

interface ServiceHandler<T> {

    fun start(key: T, serviceAction: String)
}