package dev.kosmx.needle.dbGen.db

import dev.kosmx.needle.dbGen.dsl.KDSL
import dev.kosmx.needle.matcher.result.Severity
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

fun KDSL.skyrage() {
    malwareId = "Skyrage"
    type = Severity.SEVERE

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

    "Upd2" byteCodeEntry {
        autoFilerInstructions = true
        insn(
            MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;"),
            TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String"),
            TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"),
            MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
            MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Base64", "getEncoder", "()Ljava/util/Base64\$Encoder;"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Base64\$Encoder", "encodeToString", "([B)Ljava/lang/String;"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;"),
            TypeInsnNode(Opcodes.NEW, "java/lang/String"),
            MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/File", "getPath", "()Ljava/lang/String;"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Runtime", "exec", "([Ljava/lang/String;)Ljava/lang/Process;"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Process", "waitFor", "()I"),
            MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/File", "delete", "()Z"),
        )
    }

    "URL1" byteCodeEntry {
        insn(
            LdcInsnNode("connect.skyrage.de")
        )
    }
    "URL2" byteCodeEntry {
        insn(
            LdcInsnNode("t23e7v6uz8idz87ehugwq.skyrage.de")
        )
    }
}