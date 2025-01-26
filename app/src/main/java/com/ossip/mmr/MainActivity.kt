package com.ossip.mmr

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main)

        val spinnerTemplate = findViewById<Spinner>(R.id.spinnerTemplate)
        val editTextDelay = findViewById<EditText>(R.id.editTextDelay)
        val editTextCount = findViewById<EditText>(R.id.editTextCount)

        val btnSchedule = findViewById<LinearLayout>(R.id.btnSchedule)
        val TextSchedule = findViewById<TextView>(R.id.TextSchedule)
        val ImgSchedule = findViewById<ImageView>(R.id.ImgSchedule)

        val editButton = findViewById<RelativeLayout>(R.id.editButton)
        val editButtonIcon = findViewById<ImageButton>(R.id.editButtonIcon)

        val refreshButton = findViewById<RelativeLayout>(R.id.refreshButton)
        val refreshButtonIcon = findViewById<ImageButton>(R.id.refreshButtonIcon)

        val dashboards = findViewById<TextView>(R.id.dashboards)
        dashboards.setOnClickListener { Toast.makeText(this, "Arrived in next version...", Toast.LENGTH_SHORT).show() }

        SpinnerLoader(spinnerTemplate)

        editButton.setOnClickListener { startActivity(Intent(this, TemplatesActivity::class.java)) }
        editButtonIcon.setOnClickListener { startActivity(Intent(this, TemplatesActivity::class.java)) }

        refreshButton.setOnClickListener {
            SpinnerLoader(spinnerTemplate)
            Toast.makeText(this, "Templates refreshed", Toast.LENGTH_SHORT).show()
        }
        refreshButtonIcon.setOnClickListener {
            SpinnerLoader(spinnerTemplate)
            Toast.makeText(this, "Templates refreshed", Toast.LENGTH_SHORT).show()
        }

        btnSchedule.setOnClickListener {
            if (!NotificationService.isActivityRunning) {
                val template = spinnerTemplate.selectedItem.toString()
                val delay = editTextDelay.text.toString().toIntOrNull() ?: 0
                val count = editTextCount.text.toString().toIntOrNull() ?: 1

                NotificationService.scheduleNotifications(this, this, template, delay, count)
            } else {
                NotificationService.stopped = true
                runOnUiThread {
                    TextSchedule.text = "Send Notifications"
                    btnSchedule.background = getDrawable(R.drawable.roundcorner_button_blue)
                    editButton.background = getDrawable(R.drawable.roundcorner_edit_blue)
                    refreshButton.background = getDrawable(R.drawable.roundcorner_edit_blue)
                    ImgSchedule.background = getDrawable(R.drawable.notifon)
                }
            }
        }
    }

    fun SpinnerLoader(spinner: Spinner) {
        val templates = TemplateManager.loadTemplates(this)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, templates.keys.toList())
        spinner.adapter = adapter
    }
}
