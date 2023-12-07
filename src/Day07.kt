enum class RANK {
    HIGH,
    PAIR,
    TWO_PAIR,
    TRIO,
    FULL_HOUSE,
    QUAD,
    FIVE
}

fun main() {
    open class Hand(val cards: String, val bid: Int): Comparable<Hand> {
        open val order = "23456789TJQKA"
        open val joker: Char? = null

        fun rankHand(): RANK {
            val grouped = HashMap<Char, Int>()
            cards.forEach {
                if (!grouped.containsKey(it)) {
                    grouped[it] = 0
                }
                grouped[it] = grouped[it]!! + 1
            }
            val jokers = if (joker != null) {
                grouped.getOrDefault(joker, 0)
            } else {
                0
            }

            if (grouped.size == 1) { // AAAAA
                return RANK.FIVE
            }
            if (grouped.size == 2) {
                if (jokers > 0) {
                    return RANK.FIVE
                }
                // QUAD
                if (grouped.containsValue(4)) { // ABBBB
                    return RANK.QUAD
                }
                // FULL_HOUSE
                return RANK.FULL_HOUSE // AABBB
            }
            if (grouped.size == 3) {
                // TRIO
                if (grouped.containsValue(3)) { // AAABC
                    if (jokers > 0) {
                        return RANK.QUAD
                    }
                    return RANK.TRIO
                }
                // AABBC
                if (jokers == 2) {
                    return RANK.QUAD
                }
                if (jokers == 1) {
                    return RANK.FULL_HOUSE
                }
                // TWO PAIR
                return RANK.TWO_PAIR
            }
            if (grouped.size == 4) { // AABCD
                if (jokers > 0) {
                    return RANK.TRIO
                }
                return RANK.PAIR
            }
            if (grouped.size == 5) {
                if (jokers > 0) {
                    return RANK.PAIR
                }
                return RANK.HIGH
            }
            return RANK.HIGH
        }

        open val rank = rankHand()

        override fun compareTo(other: Hand): Int = when {
            this.rank != other.rank -> this.rank compareTo other.rank // compareTo() in the infix form
            this.cards[0] != other.cards[0] -> order.indexOf(this.cards[0]) compareTo order.indexOf(other.cards[0])
            this.cards[1] != other.cards[1] -> order.indexOf(this.cards[1]) compareTo order.indexOf(other.cards[1])
            this.cards[2] != other.cards[2] -> order.indexOf(this.cards[2]) compareTo order.indexOf(other.cards[2])
            this.cards[3] != other.cards[3] -> order.indexOf(this.cards[3]) compareTo order.indexOf(other.cards[3])
            this.cards[4] != other.cards[4] -> order.indexOf(this.cards[4]) compareTo order.indexOf(other.cards[4])
            else -> 0
        }
    }

    fun part1(input: List<String>): Int {
        return input.map { it.split(" ") }
            .map { Hand(it[0], it[1].toInt()) }
            .sorted()
            .mapIndexed { index, hand -> hand.bid * (index + 1) }
            .sum()
    }

    fun part2(input: List<String>): Int {
        class JokersHand(cards: String, bid: Int): Hand(cards, bid) {
            override val order = "J23456789TQKA"
            override val joker: Char = 'J'
            override val rank = rankHand()
        }

        return input.map { it.split(" ") }
            .map { JokersHand(it[0], it[1].toInt()) }
            .sorted()
            .mapIndexed { index, hand -> hand.bid * (index + 1) }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}