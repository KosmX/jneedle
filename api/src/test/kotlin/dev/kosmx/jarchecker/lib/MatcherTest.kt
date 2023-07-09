package dev.kosmx.jarchecker.lib

import dev.kosmx.needle.util.Word
import dev.kosmx.needle.util.match
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class MatcherTest {

    @Test
    fun testMatcher() {
        // a trivial case
        Assertions.assertEquals(-1, Word("abcdabd".toCharArray().toTypedArray()).match("asfasdgfasfga".toList()))
        Assertions.assertEquals(-1, Word("abacababc".toCharArray().toTypedArray()).match("y;kmlkasnfvojbiagjnaobnaabaavaba".toList()))
        Assertions.assertEquals(0, Word("asd".toCharArray().toTypedArray()).match("asd".toList()))

        Assertions.assertEquals(-1, Word("asd".toCharArray().toTypedArray()).match("asb".toList()))


        val word = Word("asdad".toCharArray().toTypedArray())
        val string = "kasjbdkjbgfkjanbfkabnsfinasofnoasdadasdasdasdasdasdadasssad"

        Assertions.assertEquals(31, word.match(string.toList()))


        Assertions.assertEquals(15, Word("123".toCharArray().toTypedArray()).match("asdag3gd12 4a12123sdfaf".toList()))
    }
}