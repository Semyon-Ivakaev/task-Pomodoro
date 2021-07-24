package com.example.rsshool2021_android_task_pomodoro.stopwatch.utils

import com.example.rsshool2021_android_task_pomodoro.stopwatch.Stopwatch

interface StopwatchListener {
    fun replace(stopwatch: Stopwatch)
    fun delete(stopwatch: Stopwatch)
}