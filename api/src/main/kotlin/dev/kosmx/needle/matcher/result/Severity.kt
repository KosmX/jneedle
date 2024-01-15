package dev.kosmx.needle.matcher.result

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Severity(val level: Int) {

    /**
     * It is very likely not a threat, but something may be an issue
     */
    @SerialName("INFO")
    INFORMATION(0),

    /**
     * Threat isn't dangerous, something unwanted like data logging/etc, no token logging or backdoor access
     */
    @SerialName("POTENTIAL")
    WARNING(1),

    /**
     * Serious threats like backdoor, token loggers, something that you definitely not want to run on your machine
     */
    @SerialName("MALWARE")
    SEVERE(2),
}
