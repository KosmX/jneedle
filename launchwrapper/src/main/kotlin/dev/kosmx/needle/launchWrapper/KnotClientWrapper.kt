@file:JvmName("KnotClientWrapper")
package dev.kosmx.needle.launchWrapper


//Add this to PrismLauncher config
// "mainClass": "dev.kosmx.needle.launchWrapper.KnotClientWrapper",
fun main(args: Array<String>) {
    JavaAgentLauncher.checkMinecraft()

    val knot = Class.forName("net.fabricmc.loader.impl.launch.knot.KnotClient")
    val main = knot.getMethod("main", Array<String>::class.java)
    main.invoke(null, args)
}