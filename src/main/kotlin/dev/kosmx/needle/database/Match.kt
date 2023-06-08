package dev.kosmx.needle.database

import dev.kosmx.needle.JarCheckResult
import org.objectweb.asm.tree.AbstractInsnNode

interface Match {

    fun checkResult() : JarCheckResult
}