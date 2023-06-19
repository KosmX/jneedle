package dev.kosmx.needle.util

/**
 * O(n+m) pattern matching with Knuth-Morris-Pratt algo
 *
 * @param T has to be comparable (== or .equals has to work as data-classes)
 */
class Word<T>(val word: Array<T>, private val comparator: (T, T) -> Boolean = { a, b -> a == b }) {
    init {
        require(word.isNotEmpty()) { "Matcher can't match empty word" }
    }
    private val pattern = Pattern()


    fun matcher() = Matcher(pattern)


    inner class Matcher internal constructor (private val pattern: Pattern) {
        val word: Array<T>
            get() = this@Word.word

        private var pos = -1

        /**
         * Matcher check next entry
         * @return true if full match present
         */
        fun checkNextChar(char: T): Boolean {
            pos++
            findLoop@ while (pos >= 0) {
                if (comparator(char, word[pos]) ) {
                    break@findLoop
                } else {
                    pos = pattern.table[pos]
                }
            }
            if (pos + 1 == word.size) {
                pos = pattern.table[pos + 1]
                return true
            }
            return false
        }
    }

    inner class Pattern internal constructor() {
        internal val table: IntArray = IntArray(word.size + 1)

        //https://en.wikipedia.org/wiki/Knuth%E2%80%93Morris%E2%80%93Pratt_algorithm#Description_of_pseudocode_for_the_table-building_algorithm
        init {
            table[0] = -1
            var cnd = 0
            for (i in word.indices.drop(1)) {
                if (comparator(word[i], word[cnd])) {
                    table[i] = table[cnd]
                } else {
                    table[i] = cnd
                    while (cnd >= 0 && !comparator(word[cnd], word[i])) {
                        cnd = table[cnd]
                    }
                }
                cnd++
            }
            table[word.size] = cnd
        }
    }
}

fun <T> Word<T>.match(iterable: Iterable<T>) = match(iterable.asSequence())

fun <T> Word<T>.match(sequence: Sequence<T>): Int = match(sequence.iterator())

fun <T> Word<T>.match(sequence: Iterator<T>): Int {
    val m = matcher()
    var pos = 0
    sequence.forEach {
        if (m.checkNextChar(it)) {
            return@match pos - word.size + 1
        }
        pos++
    }
    return -1
}
