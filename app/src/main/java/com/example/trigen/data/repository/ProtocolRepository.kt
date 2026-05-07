package com.example.trigen.data.repository

import com.example.trigen.data.local.dao.ProtocolDao
import com.example.trigen.data.local.entity.ProtocolEntity
import com.example.trigen.data.model.StepJson
import com.example.trigen.screens.protocol.InjuryProtocol
import com.example.trigen.screens.protocol.ProtocolStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton
import com.example.trigen.data.repository.getSeverityColor
import com.example.trigen.data.repository.getIconForInjury

@Singleton
class ProtocolRepository @Inject constructor(
    private val protocolDao: ProtocolDao
) {
    suspend fun getProtocol(injuryType: String): InjuryProtocol? =
        withContext(Dispatchers.IO) {
            protocolDao.getProtocol(injuryType.uppercase())?.toInjuryProtocol()
        }

    private fun ProtocolEntity.toInjuryProtocol(): InjuryProtocol {
        val stepsArray = JSONArray(stepsJson)
        val steps = mutableListOf<ProtocolStep>()
        for (i in 0 until stepsArray.length()) {
            val obj = stepsArray.getJSONObject(i)
            steps.add(
                ProtocolStep(
                    stepNumber = obj.getInt("stepNumber"),
                    title = obj.getString("title"),
                    instruction = obj.getString("instruction"),
                    warning = if (obj.isNull("warning")) null
                    else obj.getString("warning")
                )
            )
        }

        val doNotArray = JSONArray(doNotJson)
        val doNot = mutableListOf<String>()
        for (i in 0 until doNotArray.length()) {
            doNot.add(doNotArray.getString(i))
        }

        return InjuryProtocol(
            injuryType = injuryType,
            displayName = displayName,
            severity = severity,
            severityColor = getSeverityColor(severity),
            icon = getIconForInjury(injuryType),
            steps = steps,
            doNot = doNot,
            callEmergency = callEmergency
        )
    }
}