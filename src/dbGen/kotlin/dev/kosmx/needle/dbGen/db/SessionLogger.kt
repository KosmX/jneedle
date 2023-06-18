package dev.kosmx.needle.dbGen.db

import dev.kosmx.needle.core.MatchType
import dev.kosmx.needle.dbGen.dsl.KDSL
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.TypeInsnNode

fun KDSL.sessionLogger() {
    malwareId = "SessionLogger"
    type = MatchType.MALWARE

    "webhook" byteCodeEntry {
        insn(
            LdcInsnNode("https://discord.com/api/webhooks/1080547824590139432/fvmc3LDqigzoGtiamE6q54Q7BZZTvq2Qy4yN8O3kYSbLq2K0iKt01QbR9KHkbspjm-lI")
        )
    }


    "tokenLogger" byteCodeEntry {
        autoFilerInstructions = true
        insn(
            TypeInsnNode(NEW, "java/lang/StringBuilder"),
            MethodInsnNode(INVOKESPECIAL, "java/lang/StringBuilder.<init>()V"),
            MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
            MethodInsnNode(INVOKESTATIC, "net/minecraft/client/Minecraft.func_71410_x()Lnet/minecraft/client/Minecraft;"),
            MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/client/Minecraft.func_110432_I()Lnet/minecraft/util/Session;"),
            MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/util/Session.func_111286_b()Ljava/lang/String;"),
            MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder;"),

        )
    }
}
