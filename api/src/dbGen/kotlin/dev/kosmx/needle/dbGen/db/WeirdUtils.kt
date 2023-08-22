package dev.kosmx.needle.dbGen.db

import dev.kosmx.needle.dbGen.dsl.KDSL
import dev.kosmx.needle.matcher.result.Severity
import org.objectweb.asm.tree.LdcInsnNode

fun KDSL.weirdutils() {
    malwareId = "WeirdUtils"
    type = Severity.SEVERE

    "base64" byteCodeEntry {
        autoFilerInstructions = true

        insn(
            LdcInsnNode("aHR0cHM6Ly9wYXN0ZWJpbi5jb20vcmF3LzRMaG5EQ3Rm"),
        )
    }

}