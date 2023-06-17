package dev.kosmx.needle.matcher.impl

import dev.kosmx.needle.matcher.FileMatchContext
import dev.kosmx.needle.matcher.IJarMatcher
import dev.kosmx.needle.matcher.IMatchRule

/**
 * Match rule defined by database. It should be serializable // TODO
 *
 */
class DataMatchRule : IMatchRule {



    override fun getJarMatcher(context: FileMatchContext): IJarMatcher? {
        TODO("Not yet implemented")
    }

}