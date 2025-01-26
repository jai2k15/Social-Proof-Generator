package com.ossip.mmr

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object TemplateManager {

    fun loadTemplates(context: Context): MutableMap<String, MutableMap<String, Any>> {
        val file = File(context.filesDir, "templates.json")
        if (!file.exists()) {
            val templateJson = context.assets.open("templates.json").bufferedReader().use { it.readText() }
            file.writeText(templateJson)
        }
        val jsonString = file.readText()
        val type = object : TypeToken<Map<String, Map<String, Any>>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

    fun saveTemplatesToJsonFile(context: Context, templates: MutableMap<String, MutableMap<String, Any>>) {
        val json = Gson().toJson(templates)
        val file = File(context.filesDir, "templates.json")
        file.writeText(json)
    }

    fun resetTemplates(context: Context) {
        val file = File(context.filesDir, "templates.json")
        if (file.exists()) {
            file.delete()
            Log.d("Templates", "Configuration file deleted: ${file.absolutePath}")
        }
    }
}