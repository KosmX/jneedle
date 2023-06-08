package dev.kosmx.needle.core

import dev.kosmx.needle.*
import me.coley.cafedude.classfile.ClassFile
import me.coley.cafedude.io.ClassFileReader
import me.coley.cafedude.io.ClassFileWriter
import me.coley.cafedude.transform.IllegalStrippingTransformer
import org.objectweb.asm.ClassReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.util.jar.JarEntry
import java.util.jar.JarInputStream


object JarChecker {

    fun checkJar(file: File): Set<JarCheckResult> {
        try {
            JarInputStream(file.inputStream()).use {
                return checkJar(it)
            }
        } catch (t: IOException) {
            log { "Failed to open $file" }
        }
        return setOf()
    }

    private fun checkJar(jar: JarInputStream): Set<JarCheckResult> {
        val results = mutableSetOf<JarCheckResult>()
        for ((jarEntry, bytes) in jar) {
            if (jarEntry.name.endsWith(".class")) {
                try {
                    results += checkClassFile(bytes.value, jarEntry)
                    continue
                } catch (e: IOException) {
                    results += JarCheckMatch(MatchType.POTENTIAL, "Illegal .class file: ${jarEntry.name}")
                }
            } else if (jarEntry.name.endsWith(".jar")) {
                try {
                    JarInputStream(ByteArrayInputStream(bytes.value)).use { jarStream ->
                        results += checkJar(jarStream) // check nested jars recursively
                    }
                    continue
                } catch (t: Throwable) {
                    results += JarCheckMatch(MatchType.INFO, "Failed to open nested jar: ${t.message}")
                    log(LogLevel.Info) {
                        t.printStackTrace()
                        t.message ?: ""
                    }
                }
            }

            try {
                results += AssetChecker.checkAsset(bytes, jarEntry)
            } catch (t: Throwable) {
                results += JarCheckMatch(MatchType.INFO, "Asset checker failed: ${t.message}")
            }

        }

        return results
    }

    private fun checkClassFile(bytes: ByteArray, jarEntry: JarEntry): Set<JarCheckResult> {
        try {
            val classReader = ClassReader(bytes)

            return ClassChecker.checkClass(classReader)
        } catch (t: Throwable) {
            return try {

                val cr = ClassFileReader()
                val classFile: ClassFile = cr.read(bytes)
                IllegalStrippingTransformer(classFile).transform()
                val classBytes = ClassFileWriter().write(classFile)
                ClassChecker.checkClass(ClassReader(classBytes)) + JarCheckMatch(MatchType.POTENTIAL, "unlikely obfuscated")

            } catch (t: Throwable) {
                throw IOException("can't read class file")
            }
        }
    }
}

fun JarInputStream.asSequence() = sequence<Pair<JarEntry, Lazy<ByteArray>>> {
    val jar = this@asSequence
    var entry: JarEntry? = nextJarEntry
    while(entry != null) {
        val bytes = lazy { jar.readAllBytes() }
        yield(entry to bytes)
        entry = jar.nextJarEntry
    }
}

operator fun JarInputStream.iterator(): Iterator<Pair<JarEntry, Lazy<ByteArray>>> = this.asSequence().iterator()
