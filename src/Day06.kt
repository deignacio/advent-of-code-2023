fun main() {
    data class Race(val duration: Long, val record: Long)

    fun distance(hold: Long, total: Long): Long {
        val moving = total - hold
        return moving * hold
    }

    fun findWinners(race: Race): List<Long> {
        return (0..race.duration).filter { distance(it, race.duration) > race.record }
    }

    fun part1(input: List<String>): Int {
        val times = input[0].split(":")[1].split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
        val distances = input[1].split(":")[1].split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
        return times.mapIndexed { index, i -> Race(i, distances[index]) }
            .map { findWinners(it).size }.reduce{ a, b -> a * b }
    }

    fun part2(input: List<String>): Int {
        val time = input[0].split(":")[1].replace(" ", "").toLong()
        val distance = input[1].split(":")[1].replace(" ", "").toLong()
        return findWinners(Race(time, distance)).size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}