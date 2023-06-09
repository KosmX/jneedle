package dev.kosmx.needle.dbGen.db

import dev.kosmx.needle.core.MatchType
import dev.kosmx.needle.dbGen.dsl.KDSL
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

fun KDSL.skyrage() {
    malwareId = "Skyrage"
    type = MatchType.MALWARE

    "Upd1" byteCodeEntry {
        autoFilerInstructions = true

        insn(
            TypeInsnNode(Opcodes.NEW, "java/lang/String"),
            MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64\$Decoder;"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Base64\$Decoder", "decode", "(Ljava/lang/String;)[B"),
            MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/File", "getPath", "()Ljava/lang/String;"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Runtime", "exec", "([Ljava/lang/String;)Ljava/lang/Process;"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Process", "waitFor", "()I"),
            MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/File", "delete", "()Z"),
            MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "getBytes", "()[B"),
            MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64\$Decoder;"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Base64\$Decoder", "decode", "(Ljava/lang/String;)[B"),

        )
    }
}