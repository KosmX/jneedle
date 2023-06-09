@file:JvmName("KnotClientWrapper")
package dev.kosmx.needle.launchWrapper

import dev.kosmx.needle.CheckWrapper
import dev.kosmx.needle.core.MatchType
import java.lang.Exception
import kotlin.io.path.Path

fun main(args: Array<String>) {
    CheckWrapper.init()

    val result = CheckWrapper.checkPathBlocking(Path("mods"))
    if (result.any { pair -> pair.second.any { it.status != MatchType.INFO } }) {

        result.forEach { (file, results) ->
            println("$file matches ${results.map { it.getMessage() }}")
         }

        throw Exception("Malware detected, please remove the malicious file!")
    }

    val knot = Class.forName("net.fabricmc.loader.impl.launch.knot.KnotClient")
    val main = knot.getMethod("main", Array<String>::class.java)
    main.invoke(null, args)
}