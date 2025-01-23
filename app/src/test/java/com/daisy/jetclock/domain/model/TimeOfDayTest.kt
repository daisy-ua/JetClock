package com.daisy.jetclock.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeOfDayTest {

    @Test
    fun `convert 24-hour format midnight to 12-hour format`() {
        val time = TimeOfDay(0, 0, null)
        val result = time.to12HourFormat()
        assertEquals(TimeOfDay(12, 0, MeridiemOption.AM), result)
    }

    @Test
    fun `convert 24-hour format noon to 12-hour format`() {
        val time = TimeOfDay(12, 0, null)
        val result = time.to12HourFormat()
        assertEquals(TimeOfDay(12, 0, MeridiemOption.PM), result)
    }

    @Test
    fun `convert 24-hour format afternoon to 12-hour format`() {
        val time = TimeOfDay(15, 30, null)
        val result = time.to12HourFormat()
        assertEquals(TimeOfDay(3, 30, MeridiemOption.PM), result)
    }

    @Test
    fun `convert 24-hour format morning to 12-hour format`() {
        val time = TimeOfDay(9, 45, null)
        val result = time.to12HourFormat()
        assertEquals(TimeOfDay(9, 45, MeridiemOption.AM), result)
    }

    @Test
    fun `convert 12-hour format AM to 24-hour format`() {
        val time = TimeOfDay(9, 45, MeridiemOption.AM)
        val result = time.to24HourFormat()
        assertEquals(TimeOfDay(9, 45, null), result)
    }

    @Test
    fun `convert 12-hour format PM to 24-hour format`() {
        val time = TimeOfDay(3, 15, MeridiemOption.PM)
        val result = time.to24HourFormat()
        assertEquals(TimeOfDay(15, 15, null), result)
    }

    @Test
    fun `convert 12-hour format midnight to 24-hour format`() {
        val time = TimeOfDay(12, 0, MeridiemOption.AM)
        val result = time.to24HourFormat()
        assertEquals(TimeOfDay(0, 0, null), result)
    }

    @Test
    fun `convert 12-hour format noon to 24-hour format`() {
        val time = TimeOfDay(12, 0, MeridiemOption.PM)
        val result = time.to24HourFormat()
        assertEquals(TimeOfDay(12, 0, null), result)
    }

    @Test
    fun `convert already 12-hour format to 12-hour format`() {
        val time = TimeOfDay(3, 30, MeridiemOption.PM)
        val result = time.to12HourFormat()
        assertEquals(time, result)
    }

    @Test
    fun `convert already 24-hour format to 24-hour format`() {
        val time = TimeOfDay(15, 30, null)
        val result = time.to24HourFormat()
        assertEquals(time, result)
    }
}