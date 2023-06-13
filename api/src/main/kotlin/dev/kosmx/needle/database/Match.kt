package dev.kosmx.needle.database

import dev.kosmx.needle.core.JarCheckResult

interface Match {

    fun checkResult() : JarCheckResult
}