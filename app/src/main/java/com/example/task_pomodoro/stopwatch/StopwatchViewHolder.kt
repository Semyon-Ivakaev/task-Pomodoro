package com.example.rsshool2021_android_task_pomodoro.stopwatch

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rsshool2021_android_task_pomodoro.R
import com.example.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import com.example.rsshool2021_android_task_pomodoro.stopwatch.utils.StopwatchListener

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
) : RecyclerView.ViewHolder(binding.root) {

    private var timer: Stopwatch? = null

    fun bind(stopwatch: Stopwatch) {

        if (timer != stopwatch) {
            timer?.tickCallback = null
            timer?.stateCallback = null
            timer = stopwatch

            stopwatch.tickCallback = {
                setTimerText(stopwatch)
                setTimerProgress(stopwatch)
            }
            stopwatch.stateCallback = {
                setTimerState(stopwatch)
            }
        }

        binding.customView.setPeriod(stopwatch.startPeriod)
        binding.customView.setCurrent(stopwatch.progress)

        setTimerState(stopwatch)

        initButtonsListeners(stopwatch)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                stopwatch.stop()
            } else {
                stopwatch.start()
            }
            listener.replace(stopwatch)
        }

        binding.restartButton.setOnClickListener {
            stopwatch.reset()
            listener.replace(stopwatch)
        }

        binding.deleteButton.setOnClickListener {
            listener.delete(stopwatch)
        }
    }

    private fun setTimerState(stopwatch: Stopwatch) {
        if (timer?.isStarted == true) {
            binding.startPauseButton.setImageResource(R.drawable.ic_baseline_pause_24)
            (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
            binding.blinkingIndicator.isInvisible = false
        } else {
            binding.startPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            binding.blinkingIndicator.isInvisible = true
            (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
        }

        setTimerText(stopwatch)
        setTimerProgress(stopwatch)

        if (timer?.progress == 0L) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                itemView.setBackgroundColor(itemView.resources.getColor(R.color.red, null))
                binding.startPauseButton.setBackgroundColor(itemView.resources.getColor(R.color.red, null))
                binding.restartButton.setBackgroundColor(itemView.resources.getColor(R.color.red, null))
                binding.deleteButton.setBackgroundColor(itemView.resources.getColor(R.color.red, null))
            }
        } else {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    private fun setTimerText(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.text
    }

    private fun setTimerProgress(stopwatch: Stopwatch) {
        binding.customView.setCurrent(stopwatch.progress)
    }
}