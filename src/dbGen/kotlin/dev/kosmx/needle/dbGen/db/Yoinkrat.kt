package dev.kosmx.needle.dbGen.db

import dev.kosmx.needle.core.MatchType
import dev.kosmx.needle.dbGen.dsl.KDSL
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

fun KDSL.yoinkrat() {
    malwareId = "YoinkRat"
    type = MatchType.MALWARE

    /*"CL1" byteCodeEntry {
        autoFilerInstructions = true
        insn(
            MethodInsnNode(INVOKEINTERFACE, "java/util/List", "stream", "()Ljava/util/stream/Stream;"),
            //dynamic
            MethodInsnNode(INVOKEINTERFACE,"java/util/stream/Stream", "map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;"),
            // dynamic
            MethodInsnNode(INVOKEINTERFACE, "java/util/stream/Stream", "filter", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"),
            MethodInsnNode(INVOKEINTERFACE, "java/util/stream/Stream", "filter", "(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"),
            MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;"),
            MethodInsnNode(INVOKEINTERFACE, "java/util/stream/Stream", "forEach", "(Ljava/util/function/Consumer;)V"),
            MethodInsnNode(INVOKESTATIC, "java/util/stream/Stream", "forEach", "(Ljava/util/function/Consumer;)V"),
            // invoke static, wildcard support required MethodInsnNode(INVOKESTATIC, "*", "removeDuplicates", "(Ljava/util/List;)Ljava/util/List;"),
        )
    }*/ // TODO fix this once proper wildcard and regex support is working

    "WEBHOOKS" byteCodeEntry {
        autoFilerInstructions = true
        insn(
            MethodInsnNode(INVOKESTATIC, "java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;"),
            TypeInsnNode(NEW, "java/lang/String"),
            MethodInsnNode(INVOKESTATIC,"java/util/Base64", "getDecoder", "()Ljava/util/Base64\$Decoder;"),
            TypeInsnNode(NEW, "java/util/Random"),
            MethodInsnNode(INVOKESPECIAL, "java/util/Random", "<init>", "()V"),
            MethodInsnNode(INVOKEVIRTUAL, "java/util/Random", "nextInt", "(I)I"),
            MethodInsnNode(INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;"),
            TypeInsnNode(CHECKCAST, "java/lang/String"),
            MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B"),
            MethodInsnNode(INVOKEVIRTUAL, "java/util/Base64\$Decoder", "decode", "([B)[B"),
            MethodInsnNode(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"),
            TypeInsnNode(NEW, "java/lang/Thread"),
            MethodInsnNode(INVOKESPECIAL, "java/lang/Thread", "<init>", "(Ljava/lang/Runnable;)V"),
            MethodInsnNode(INVOKEVIRTUAL, "java/lang/Thread", "start", "()V"),
        )
    }


}