fun main() {
    fun distances(series: List<Int>): List<Int> {
        return series.mapIndexed { index, i -> if (index + 1 < series.size) {
                series[index + 1] - i
            } else {
                null
            }
        }.filterNotNull()
    }

    fun part1(input: List<String>): Int {
        val allSeries = input.map { it -> it.split(" ").map { it.toInt() } }
        fun evolve(series: List<Int>): Int {
            return if (series.all { it == 0 }) {
                0
            } else {
                evolve(distances(series)) + series.last()
            }
        }
        return allSeries.sumOf { evolve(it) }
    }

    fun part2(input: List<String>): Int {
        val allSeries = input.map { it -> it.split(" ").map { it.toInt() } }
        fun devolve(series: List<Int>): Int {
            return if (series.all { it == 0 }) {
                0
            } else {
                series.first() - devolve(distances(series))
            }
        }
        return allSeries.sumOf { devolve(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}