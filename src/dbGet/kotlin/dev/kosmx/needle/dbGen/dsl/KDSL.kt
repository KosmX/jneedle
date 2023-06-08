package dev.kosmx.needle.dbGen.dsl

import dev.kosmx.needle.MatchType
import dev.kosmx.needle.database.Info
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ACC_PRIVATE
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import java.nio.file.Path
import java.time.Clock
import java.time.Instant
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

interface Entry {
    /**
     * Write file and returns filename for later use
     */
    fun generate(outputPath: Path) : String
}

var idTracker = 0

@SampleDSL
class KDSL(private val entries: MutableList<Entry>) {
    var malwareId: String = "none"

    var type: MatchType = MatchType.MALWARE

    fun byteCodeEntry(block: ByteCodeEntry.() -> Unit) {
        entries += ByteCodeEntry().also(block)
    }

    infix fun String.byteCodeEntry(block: ByteCodeEntry.() -> Unit) {
        entries += ByteCodeEntry().also { it.matchId = this }.also(block)
    }


    @SampleDSL
    inner class ByteCodeEntry : Entry {
        var className: String? = null
        var matchId: String = (idTracker++).toString()

        val fileName: String
            get() = "${this@KDSL.malwareId}-$matchId.jasm"
        private var instructions = mutableListOf<AbstractInsnNode>()


        fun insn(vararg i: AbstractInsnNode) {
            instructions += i
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun generate(outputPath: Path): String {
            JarOutputStream(outputPath.resolve(fileName).toFile().outputStream()).use { jar ->
                val classNode = ClassNode()
                classNode.name = "content"
                classNode.methods.add(MethodNode(ACC_STATIC, "<clinit>", "()V", null, null).apply {
                    instructions.add(InsnNode(Opcodes.ICONST_1))
                    instructions.add(InsnNode(Opcodes.ICONST_0))
                    instructions.add(InsnNode(Opcodes.IDIV))
                    instructions.add(VarInsnNode(Opcodes.ISTORE, 1))
                    instructions.add(InsnNode(Opcodes.RETURN))
                })

                classNode.methods.add(MethodNode(ACC_PRIVATE, "pattern", "()V", null, null).apply {
                    this@ByteCodeEntry.instructions.forEach {
                        instructions.add(it)
                    }
                })
                val entry = JarEntry("content.class")
                entry.time = Instant.now().epochSecond

                val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS)
                classNode.accept(writer)
                jar.putNextEntry(entry)
                jar.write(writer.toByteArray())
                jar.closeEntry()

                val dataEntr = JarEntry("info.json")
                dataEntr.time = Instant.now().epochSecond
                jar.putNextEntry(dataEntr)
                Json.encodeToStream(Info(this@KDSL.malwareId, this@KDSL.type, matchId = matchId), jar)
                jar.closeEntry()

            }
            return fileName
        }

    }
}

