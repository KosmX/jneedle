package dev.kosmx.needle.launchWrapper

object ParameterizedWrapper {
    @JvmStatic
    fun main(args: Array<String>) {
        val launchClass = System.getProperty("dev.kosmx.jneedle.launchClass")!!

        JavaAgentLauncher.checkMinecraft()

        val knot = Class.forName(launchClass)
        val main = knot.getMethod("main", Array<String>::class.java)
        main.invoke(null, args)
    }
}