package com.example.trigen.data.seeder

import android.content.Context
import com.example.trigen.data.local.dao.ProtocolDao
import com.example.trigen.data.local.entity.ProtocolEntity
import com.example.trigen.data.model.ProtocolJson
import com.example.trigen.data.model.StepJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProtocolSeeder @Inject constructor(
    private val context: Context,
    private val protocolDao: ProtocolDao
) {
    suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        val count = protocolDao.getCount()
        if (count == 0) {
            val protocols = loadFromAssets()
            val entities = protocols.map { it.toEntity() }
            protocolDao.insertAll(entities)
        }
    }

    private fun loadFromAssets(): List<ProtocolJson> {
        val json = context.assets.open("protocols.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonArray = JSONArray(json)
        val protocols = mutableListOf<ProtocolJson>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            protocols.add(parseProtocol(obj))
        }
        return protocols
    }

    private fun parseProtocol(obj: JSONObject): ProtocolJson {
        val stepsArray = obj.getJSONArray("steps")
        val steps = mutableListOf<StepJson>()
        for (i in 0 until stepsArray.length()) {
            val stepObj = stepsArray.getJSONObject(i)
            steps.add(
                StepJson(
                    stepNumber = stepObj.getInt("stepNumber"),
                    title = stepObj.getString("title"),
                    instruction = stepObj.getString("instruction"),
                    warning = if (stepObj.isNull("warning")) null
                    else stepObj.getString("warning")
                )
            )
        }

        val doNotArray = obj.getJSONArray("doNot")
        val doNot = mutableListOf<String>()
        for (i in 0 until doNotArray.length()) {
            doNot.add(doNotArray.getString(i))
        }

        return ProtocolJson(
            injuryType = obj.getString("injuryType"),
            displayName = obj.getString("displayName"),
            severity = obj.getString("severity"),
            source = obj.getString("source"),
            callEmergency = obj.getBoolean("callEmergency"),
            steps = steps,
            doNot = doNot
        )
    }

    private fun ProtocolJson.toEntity(): ProtocolEntity {
        val stepsJson = JSONArray().apply {
            steps.forEach { step ->
                put(JSONObject().apply {
                    put("stepNumber", step.stepNumber)
                    put("title", step.title)
                    put("instruction", step.instruction)
                    put("warning", step.warning ?: JSONObject.NULL)
                })
            }
        }.toString()

        val doNotJson = JSONArray().apply {
            doNot.forEach { put(it) }
        }.toString()

        return ProtocolEntity(
            injuryType = injuryType,
            displayName = displayName,
            severity = severity,
            source = source,
            callEmergency = callEmergency,
            stepsJson = stepsJson,
            doNotJson = doNotJson
        )
    }
}