package com.example.rsshool2021_android_task_pomodoro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding
import com.example.rsshool2021_android_task_pomodoro.services.ForegroundService
import com.example.rsshool2021_android_task_pomodoro.stopwatch.Stopwatch
import com.example.rsshool2021_android_task_pomodoro.stopwatch.StopwatchAdapter
import com.example.rsshool2021_android_task_pomodoro.stopwatch.utils.StopwatchListener
import com.example.rsshool2021_android_task_pomodoro.stopwatch.utils.*

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.addNewStopwatchButton.setOnClickListener {
            val time = binding.minuteEdit.text.toString().toLongOrNull()
            if (time == null) {
                Toast.makeText(this, "Input time", Toast.LENGTH_SHORT).show()
            } else {
                stopwatches.add(Stopwatch(time * 60 * 1000))
                stopwatchAdapter.submitList(stopwatches.toList())
            }
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Log.v("AppVerbose", "Background: ON_STOP")
        val currentTime = getCurrentTime()
        if (currentTime != -1L) {
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(STARTED_TIMER_TIME_MS, currentTime)
            startService(startIntent)
        }
    }

    private fun getCurrentTime(): Long {
        stopwatches.forEach {
            if (it.isStarted) return it.progress
        }
        return -1
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.v("AppVerbose", "Background: ON_START")
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }

    override fun delete(stopwatch: Stopwatch) {
        stopwatch.stop()
        stopwatches.remove(stopwatch)
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    override fun replace(stopwatch: Stopwatch) {
        if (stopwatch.isStarted) {
            stopwatches.forEach {
                if (it.id != stopwatch.id && it.isStarted) it.stop()
            }
        }

        stopwatchAdapter.notifyDataSetChanged()
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    override fun onDestroy() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)

        super.onDestroy()
    }
}