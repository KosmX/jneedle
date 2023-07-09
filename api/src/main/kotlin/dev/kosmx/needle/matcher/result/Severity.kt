package dev.kosmx.needle.matcher.result

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
enum class Severity(val level: Int) {

    /**
     * It is very likely not a threat, but something may be an issue
     */
    @JsonNames("INFO")
    INFORMATION(0),

    /**
     * Threat isn't dangerous, something unwanted like data logging/etc, no token logging or backdoor access
     */
    @JsonNames("POTENTIAL")
    WARNING(1),

    /**
     * Serious threats like backdoor, token loggers, something that you definitely not want to run on your machine
     */
    @JsonNames("MALWARE")
    SEVERE(2),
}
