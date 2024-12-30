package com.daisy.jetclock.core.receiver

interface ServiceHandler<T> {

    fun stop(key: T)
}