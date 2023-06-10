package dev.kosmx.needle.database

import org.objectweb.asm.tree.AbstractInsnNode
import software.coley.llzip.format.model.LocalFileHeader
import java.util.jar.JarEntry

interface AssetMatch : Match {
    fun match(data: Lazy<ByteArray>, jarEntry: LocalFileHeader): Boolean
}