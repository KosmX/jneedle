package dev.kosmx.needle

import org.objectweb.asm.Opcodes
import org.slf4j.kotlin.KLogger
import org.slf4j.kotlin.getLogger

const val ASM_VERSION = Opcodes.ASM9


val logger: KLogger by getLogger("jNeedle")
