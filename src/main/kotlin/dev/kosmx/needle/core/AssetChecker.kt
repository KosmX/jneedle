package dev.kosmx.needle.core

import dev.kosmx.needle.JarCheckResult
import dev.kosmx.needle.database.AssetMatch
import java.util.jar.JarEntry

/**
 * invoked for every non-class file
 */
object AssetChecker {

    private lateinit var assets: List<AssetMatch>

    fun checkAsset(bytes: Lazy<ByteArray>, asset: JarEntry): Set<JarCheckResult> {

        val result = mutableSetOf<JarCheckResult>()


        assets.forEach { m ->
            if (m.match(bytes, asset)) {
                result += m.checkResult()
            }
        }


        return result
    }

    fun init(assetMatches: List<AssetMatch>) {
        assets = assetMatches
    }
}