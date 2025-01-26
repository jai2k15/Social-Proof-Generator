package com.ossip.mmr

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class TemplatesActivity : AppCompatActivity() {

    private lateinit var templates: MutableMap<String, MutableMap<String, Any>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_templates)


        val homeNav = findViewById<LinearLayout>(R.id.home_nav)
        templates = TemplateManager.loadTemplates(this)
        val parentLayout = findViewById<LinearLayout>(R.id.parents)
        createTemplateLayouts(this, parentLayout)
        val reset = findViewById<ImageView>(R.id.reset)
        val createTemplate = findViewById<LinearLayout>(R.id.create_template)

        reset.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to reset all templates?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    TemplateManager.resetTemplates(this)
                    templates = TemplateManager.loadTemplates(this)
                    createTemplateLayouts(this, parentLayout)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
        createTemplate.setOnClickListener { loadTemplates(this, "New Template", mutableMapOf("text" to "Content"), LayoutInflater.from(this), parentLayout) }

        homeNav.setOnClickListener {
            finish()
        }
    }

    private fun loadTemplates(context: Context, key: String, value: MutableMap<String, Any>, inflater: LayoutInflater, parentLayout: LinearLayout) {
        val templateView = inflater.inflate(R.layout.template_item, parentLayout, false)

        val titleTextView = templateView.findViewById<EditText>(R.id.template_title)
        val contentEditText = templateView.findViewById<EditText>(R.id.template_content)
        val deleteButton = templateView.findViewById<TextView>(R.id.deleteButton)


        titleTextView.setText(key as? String ?: "")
        contentEditText.setText(value["text"] as? String ?: "")

        contentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val kee = titleTextView.text.toString()
                templates[kee]?.set("text", s.toString())
                TemplateManager.saveTemplatesToJsonFile(context, templates)
            }
        })

        titleTextView.addTextChangedListener(object : TextWatcher {
            private var prev = key
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (!isUpdating) { prev = s.toString() }
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) { return }
                if (s.toString() == prev) { return
                } else if (s.toString().isEmpty()) {
                    Toast.makeText(context, "Title cannot be empty!", Toast.LENGTH_SHORT).show()
                    isUpdating = true
                    titleTextView.setText(prev)
                    titleTextView.setSelection(prev.length)
                    isUpdating = false
                } else if (templates.containsKey(s.toString())) {
                    Toast.makeText(context, "Title already exists!", Toast.LENGTH_SHORT).show()
                    isUpdating = true
                    titleTextView.setText(prev)
                    titleTextView.setSelection(prev.length)
                    isUpdating = false
                } else {
                    templates[s.toString()] = templates[prev] ?: mutableMapOf()
                    templates.remove(prev)
                    TemplateManager.saveTemplatesToJsonFile(context, templates)
                }
            }
        })

        deleteButton.setOnClickListener {
            val kee = titleTextView.text.toString()
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete this template (${kee})?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    templates.remove(kee)
                    TemplateManager.saveTemplatesToJsonFile(context, templates)
                    parentLayout.removeView(templateView)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        parentLayout.addView(templateView)
    }

    private fun createTemplateLayouts(context: Context, parentLayout: LinearLayout) {
        val inflater = LayoutInflater.from(context)
        parentLayout.removeAllViewsInLayout()

        for ((key, value) in templates) {
            loadTemplates(context, key, value, inflater, parentLayout)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TemplateManager.saveTemplatesToJsonFile(this, templates)
    }
}
