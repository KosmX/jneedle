package dev.kosmx.needle.core

import dev.kosmx.needle.*
import me.coley.cafedude.classfile.ClassFile
import me.coley.cafedude.io.ClassFileReader
import me.coley.cafedude.io.ClassFileWriter
import me.coley.cafedude.transform.IllegalStrippingTransformer
import org.objectweb.asm.ClassReader
import software.coley.llzip.ZipIO
import software.coley.llzip.format.compression.ZipCompressions
import software.coley.llzip.format.model.LocalFileHeader
import software.coley.llzip.format.model.ZipArchive
import software.coley.llzip.util.ByteData
import software.coley.llzip.util.ByteDataUtil
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.util.jar.JarEntry
import java.util.jar.JarInputStream


object JarChecker {

    fun checkJar(file: File): Set<JarCheckResult> {
        try {
            ZipIO.readJvm(file.toPath()).use { jar ->
                return CheckWrapper.checkJar(jar)
            }
        } catch (t: Throwable) {
            log {
                t.printStackTrace()
                "Failed to open $file"
            }
        }
        return setOf()
    }

    fun checkJar(jar: ZipArchive): Set<JarCheckResult> {
        val results = mutableSetOf<JarCheckResult>()
        for ((jarEntry, byteData) in jar) {
            byteData.let classEntry@{_ ->
                val bytes = lazy { ByteDataUtil.toByteArray(byteData)!! }
                if (jarEntry.fileNameAsString.endsWith(".class") || jarEntry.fileNameAsString.endsWith(".class/")) {
                    try {
                        results += checkClassFile(bytes.value, jarEntry)
                        return@classEntry
                    } catch (e: IOException) {
                        //results += JarCheckMatch(MatchType.POTENTIAL, "Illegal .class file: ${jarEntry.name}")
                    }
                } else if (jarEntry.fileNameAsString.endsWith(".jar")) {
                    try {

                        ZipIO.readJvm(byteData).use { jar ->
                            results += CheckWrapper.checkJar(jar)
                        }
                        return@classEntry
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
        }

        return results
    }

    private fun checkClassFile(bytes: ByteArray, jarEntry: LocalFileHeader): Set<JarCheckResult> {
        try {
            val classReader = ClassReader(bytes)

            return ClassChecker.checkClass(classReader)
        } catch (t: Throwable) {
            return try {

                val cr = ClassFileReader()
                val classFile: ClassFile = cr.read(bytes)
                IllegalStrippingTransformer(classFile).transform()
                val classBytes = ClassFileWriter().write(classFile)
                ClassChecker.checkClass(ClassReader(classBytes))// + JarCheckMatch(MatchType.POTENTIAL, "unlikely obfuscated")

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
        val bytes = lazy { jar.readBytes() }
        yield(entry to bytes)
        entry = jar.nextJarEntry
    }
}

operator fun JarInputStream.iterator(): Iterator<Pair<JarEntry, Lazy<ByteArray>>> = this.asSequence().iterator()

fun ZipArchive.asSequence() = localFiles.asSequence().map { it to ZipCompressions.decompress(it) }

operator fun ZipArchive.iterator(): Iterator<Pair<LocalFileHeader, ByteData>> = this.asSequence().iterator()

