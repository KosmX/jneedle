package dev.kosmx.needle.dbGen.db

import dev.kosmx.needle.dbGen.dsl.KDSL
import dev.kosmx.needle.matcher.result.Severity
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

fun KDSL.fractureiser() {
    malwareId = "fractureiser"
    type = Severity.SEVERE

    "SIG1" byteCodeEntry {
        autoFilerInstructions = true

        insn(
            TypeInsnNode(NEW, "java/lang/String"),
            MethodInsnNode(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"),
            TypeInsnNode(NEW, "java/lang/String"),
            MethodInsnNode(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"),
            MethodInsnNode(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;"),
            MethodInsnNode(
                INVOKEVIRTUAL, "java/lang/Class", "getConstructor",
                "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;"
            ),
            MethodInsnNode(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"),
            MethodInsnNode(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"),
            MethodInsnNode(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"),
            MethodInsnNode(
                INVOKESPECIAL, "java/net/URL", "<init>",
                "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V"
            ),
            MethodInsnNode(
                INVOKEVIRTUAL, "java/lang/reflect/Constructor", "newInstance",
                "([Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            MethodInsnNode(
                INVOKESTATIC, "java/lang/Class", "forName",
                "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;"
            ),
            MethodInsnNode(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"),
            MethodInsnNode(
                INVOKEVIRTUAL, "java/lang/Class", "getMethod",
                "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;"
            ),
            MethodInsnNode(
                INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke",
                "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;"
            ),
        )
    }

    "SIG2" byteCodeEntry {
        autoFilerInstructions = true

        insn(
            MethodInsnNode(INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;"),
            MethodInsnNode(INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64\$Decoder;"),
            MethodInsnNode(
                INVOKEVIRTUAL,
                "java/lang/String",
                "concat",
                "(Ljava/lang/String;)Ljava/lang/String;"
            ), // TODO:FIXME: this might not be in all of them
            MethodInsnNode(INVOKEVIRTUAL, "java/util/Base64\$Decoder", "decode", "(Ljava/lang/String;)[B"),
            MethodInsnNode(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"),
            MethodInsnNode(INVOKEVIRTUAL, "java/io/File", "getPath", "()Ljava/lang/String;"),
            MethodInsnNode(INVOKEVIRTUAL, "java/lang/Runtime", "exec", "([Ljava/lang/String;)Ljava/lang/Process;"),
        )
    }

    "SIG3" byteCodeEntry {
        insn(
            IntInsnNode(BIPUSH, 56),
            InsnNode(BASTORE),
            InsnNode(DUP),
            InsnNode(ICONST_1),
            IntInsnNode(BIPUSH, 53),
            InsnNode(BASTORE),
            InsnNode(DUP),
            InsnNode(ICONST_2),
            IntInsnNode(BIPUSH, 46),
            InsnNode(BASTORE),
            InsnNode(DUP),
            InsnNode(ICONST_3),
            IntInsnNode(BIPUSH, 50),
            InsnNode(BASTORE),
            InsnNode(DUP),
            InsnNode(ICONST_4),
            IntInsnNode(BIPUSH, 49),
            InsnNode(BASTORE),
            InsnNode(DUP),
            InsnNode(ICONST_5),
            IntInsnNode(BIPUSH, 55),
            InsnNode(BASTORE),
            InsnNode(DUP),
            IntInsnNode(BIPUSH, 6),
            IntInsnNode(BIPUSH, 46),
            InsnNode(BASTORE),
            InsnNode(DUP),
            IntInsnNode(BIPUSH, 7),
            IntInsnNode(BIPUSH, 49),
            InsnNode(BASTORE),
            InsnNode(DUP),
            IntInsnNode(BIPUSH, 8),
            IntInsnNode(BIPUSH, 52),
            InsnNode(BASTORE),
            InsnNode(DUP),
            IntInsnNode(BIPUSH, 9),
            IntInsnNode(BIPUSH, 52),
            InsnNode(BASTORE),
            InsnNode(DUP),
            IntInsnNode(BIPUSH, 10),
            IntInsnNode(BIPUSH, 46),
            InsnNode(BASTORE),
            InsnNode(DUP),
            IntInsnNode(BIPUSH, 11),
            IntInsnNode(BIPUSH, 49),
            InsnNode(BASTORE),
            InsnNode(DUP),
            IntInsnNode(BIPUSH, 12),
            IntInsnNode(BIPUSH, 51),
            InsnNode(BASTORE),
            InsnNode(DUP),
            IntInsnNode(BIPUSH, 13),
            IntInsnNode(BIPUSH, 48)
        )
    }
}
