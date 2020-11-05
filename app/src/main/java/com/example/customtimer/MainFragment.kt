package com.example.customtimer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

    companion object {
        const val TAG = "MainFragment"
        fun newInstance() = MainFragment()
    }

    private var counter = 0

    private val thread = Thread {

        var needToInterruptThread = false
        try {
            while (!Thread.currentThread().isInterrupted) {
                if (needToInterruptThread) {
                    Thread.currentThread().interrupt()
                }
                counter++

                Handler(Looper.getMainLooper()).post {
                    try {
                        tvTimer.text = counter.toString()
                    } catch (ex: NullPointerException) {
                        Log.e(
                            TAG,
                            "NullPointerException occurred, will interrupt current thread now"
                        )
                        needToInterruptThread = true
                    }
                }
                Thread.sleep(1000)
            }
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
            Log.e(TAG, "Current thread was interrupted!")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnStartTimer.setOnClickListener { startTimer() }
    }

    private fun startTimer() {
        btnStartTimer.isEnabled = false
        thread.start()
    }

    fun onBackPressed() {
        val data = Data.Builder()
            .putInt("timer", counter)
            .build()
        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        Log.d(TAG, "final timer value: $counter")
        WorkManager.getInstance(requireContext()).enqueue(request)
    }
}