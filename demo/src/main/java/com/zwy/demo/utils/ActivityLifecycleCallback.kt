package com.zwy.demo.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.zwy.demo.R

class ActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
        activity.findViewById<AppCompatTextView>(R.id.actionBarTitle)?.text = activity.title

        activity.findViewById<AppCompatImageView>(R.id.iv_back).also {
            if (activity.title == "离线鉴黄") it?.visibility = View.GONE
            it?.setOnClickListener {
                activity.finish()
            }
        }
    }
}