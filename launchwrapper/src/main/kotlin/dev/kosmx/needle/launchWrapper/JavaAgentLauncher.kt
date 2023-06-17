package dev.kosmx.needle.launchWrapper

import dev.kosmx.needle.CheckWrapper
import dev.kosmx.needle.matcher.result.Severity
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.warn
import java.lang.instrument.Instrumentation
import kotlin.io.path.Path
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

object JavaAgentLauncher {
    private var checked = false // avoid double runs
    private val logger by getLogger()

    @JvmStatic
    fun premain(agentArgs: String?, inst: Instrumentation) {
        if (!checked) {
            runCheck(agentArgs)
            checked = true
        }
    }
    @JvmStatic
    fun agentmain(agentArgs: String?, inst: Instrumentation) {
        if (!checked) {
            logger.warn { "Dynamically loading malware detection is risky, it may load after malware class is active" }
            runCheck(agentArgs)
            checked = true
        }
    }

    private fun runCheck(agentArgs: String?) {
        try {
            println("arg: $agentArgs")
            checkMinecraft(agentArgs)
        } catch (e: Exception) {
            println(e.message)
            exitProcess(-1)
        }
    }

    @OptIn(ExperimentalTime::class)
    @JvmStatic
    fun checkMinecraft(agentArgs: String? = null) {
        measureTime {
            agentArgs?.split(" ")?.let { (databaseUrl, databaseLocation) ->
                CheckWrapper.init(databaseUrl, Path(databaseLocation))
            } ?: CheckWrapper.init()

            val result = CheckWrapper.checkPathBlocking(Path("."))
            if (result.any { pair -> pair.second.any { it.severity != Severity.INFORMATION } }) {

                result.forEach { (file, results) ->
                    println("$file matches ${results.map { it.getMessage() }}")
                }

                throw Exception("Malware detected, please remove the malicious file!")
            }
        }.let { println("Malware checking done in ${it.inWholeMilliseconds} ms") }

    }
}
