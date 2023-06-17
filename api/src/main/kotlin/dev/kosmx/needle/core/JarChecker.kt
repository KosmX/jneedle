package dev.kosmx.needle.core

import dev.kosmx.needle.CheckWrapper
import me.coley.cafedude.classfile.ClassFile
import me.coley.cafedude.io.ClassFileReader
import me.coley.cafedude.io.ClassFileWriter
import me.coley.cafedude.transform.IllegalStrippingTransformer
import org.objectweb.asm.ClassReader
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.info
import org.slf4j.kotlin.warn
import software.coley.llzip.ZipIO
import software.coley.llzip.format.compression.ZipCompressions
import software.coley.llzip.format.model.LocalFileHeader
import software.coley.llzip.format.model.ZipArchive
import software.coley.llzip.util.ByteDataUtil
import java.io.File
import java.io.IOException


object JarChecker {
    private val logger by getLogger()

    fun checkJar(file: File): Set<JarCheckResult> {
        try {
            ZipIO.readJvm(file.toPath()).use { jar ->
                return CheckWrapper.checkJar(jar)
            }
        } catch (t: Throwable) {
            logger.warn {
                t.printStackTrace()
                "Failed to open $file"
            }
        }
        return setOf()
    }

    fun checkJar(jar: ZipArchive): Set<JarCheckResult> {
        val results = mutableSetOf<JarCheckResult>()
        classEntry@for (jarEntry in jar) {
            val byteData by lazy { ZipCompressions.decompress(jarEntry) }
            val bytes = lazy { ByteDataUtil.toByteArray(byteData)!! }
            if (jarEntry.fileNameAsString.endsWith(".class") || jarEntry.fileNameAsString.endsWith(".class/")) {
                try {
                    results += checkClassFile(bytes.value, jarEntry)
                    continue@classEntry
                } catch (e: IOException) {
                    //results += JarCheckMatch(MatchType.POTENTIAL, "Illegal .class file: ${jarEntry.name}")
                }
            } else if (jarEntry.fileNameAsString.endsWith(".jar")) {
                try {

                    ZipIO.readJvm(byteData).use { nestedJar ->
                        results += CheckWrapper.checkJar(nestedJar)
                    }
                    continue@classEntry
                } catch (t: Throwable) {
                    results += JarCheckMatch(MatchType.INFO, "Failed to open nested jar: ${t.message}")
                    logger.info {
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

fun ZipArchive.asSequence() = localFiles.asSequence().map { it }

operator fun ZipArchive.iterator(): Iterator<LocalFileHeader> = this.asSequence().iterator()

