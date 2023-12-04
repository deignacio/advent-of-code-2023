import kotlin.math.pow

fun main() {
    data class Card(val id: Int, val mine: Set<Int>, val winners: Set<Int>) {
        val winCount = winners.intersect(mine).size
    }

    fun parseLine(line: String): Card {
        val byColon = line.split(": ")
        val id = byColon[0].split(" ").last().toInt()
        val numbers = byColon[1].split(" | ")
        val mine = numbers[1].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
        val winners = numbers[0].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
        return Card(id, mine, winners)
    }

    fun part1(input: List<String>): Int {
        return input.map { parseLine(it).winCount }
            .filter { it > 0 }
            .sumOf { 2.0.pow(it - 1) }.toInt()
    }

    fun part2(input: List<String>): Int {
        val cards = input.map { parseLine(it) }
        val count = HashMap<Int, Int>()
        for (card in cards) {
            count[card.id] = 1
        }
        for (card in cards) {
            if (card.winCount == 0) {
                continue
            }

            for (i in 0..<card.winCount) {
                val idx = card.id + i + 1
                if (count.containsKey(idx)) {
                    count[idx] = count[idx]!! + count[card.id]!!
                }
            }
        }
        return count.values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}