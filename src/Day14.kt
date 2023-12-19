import java.util.HashMap

fun main() {
    data class Board(val input: List<String>) {
        val cache = HashMap<String, Pair<Int, Int>>()
        fun hash(): String {
            return points.keys.sortedBy { it.first }.filter { points[it] == 'O' }.toString().replace(" ", "")
        }

        fun parseBoulders(): MutableMap<Pair<Int, Int>, Char> {
            val m = HashMap<Pair<Int, Int>, Char>()
            for (y in input.indices) {
                val row = input[y]
                for (x in row.indices) {
                    m[y to x] = row[x]
                }
            }
            return m
        }
        val points = parseBoulders()

        fun load(): Int {
            return points.entries.sumOf { entry ->
                if (entry.value == 'O') {
                    input.size - entry.key.first
                } else {
                    0
                }
            }
        }

        fun dump() {
            hash().println()
            for (y in input.indices) {
                val sb = StringBuilder()
                for (x in input[0].indices) {
                    sb.append(points[y to x])
                }
                sb.toString().println()
            }
            "\n".plus(load()).plus("\n").println()
        }

        fun slideNorth() {
            points.keys.sortedBy { it.first }
                .forEach { pt ->
                    val obj = points[pt]
                    if (obj == 'O') { // roll it!
                        var moving = false
                        var next = pt.first to pt.second
                        while (next.first > 0) {
                            next = next.first - 1 to next.second
                            if (points[next] == '.') {
                                moving = true
                            } else {
                                next = next.first + 1 to next.second
                                break
                            }
                        }
                        if (moving) {
                            points[next] = obj
                            points[pt] = '.'
                        }
                    }
                }
        }

        fun slideWest() {
            points.keys.sortedBy { it.second }
                .forEach { pt ->
                    val obj = points[pt]
                    if (obj == 'O') { // roll it!
                        var moving = false
                        var next = pt.first to pt.second
                        while (next.second > 0) {
                            next = next.first to next.second - 1
                            if (points[next] == '.') {
                                moving = true
                            } else {
                                next = next.first to next.second + 1
                                break
                            }
                        }
                        if (moving) {
                            points[next] = obj
                            points[pt] = '.'
                        }
                    }
                }
        }

        fun slideSouth() {
            points.keys.sortedBy { it.first }.reversed()
                .forEach { pt ->
                    val obj = points[pt]
                    if (obj == 'O') { // roll it!
                        var moving = false
                        var next = pt.first to pt.second
                        while (next.first < input.size) {
                            next = next.first + 1 to next.second
                            if (points[next] == '.') {
                                moving = true
                            } else {
                                next = next.first - 1 to next.second
                                break
                            }
                        }
                        if (moving) {
                            points[next] = obj
                            points[pt] = '.'
                        }
                    }
                }
        }

        fun slideEast() {
            points.keys.sortedBy { it.second }.reversed()
                .forEach { pt ->
                    val obj = points[pt]
                    if (obj == 'O') { // roll it!
                        var moving = false
                        var next = pt.first to pt.second
                        while (next.first < input[0].length) {
                            next = next.first to next.second + 1
                            if (points[next] == '.') {
                                moving = true
                            } else {
                                next = next.first to next.second - 1
                                break
                            }
                        }
                        if (moving) {
                            points[next] = obj
                            points[pt] = '.'
                        }
                    }
                }
        }
        fun cycle(n: Int) {
            for (i in 1..n) {
                slideNorth()
                slideWest()
                slideSouth()
                slideEast()
                val h = hash()
                if (cache.containsKey(h)) {
                    "loop detected at ".plus(i).plus(" and ").plus(cache[h]).println()
                    cache.values.println()
                    break
                } else {
                    cache[h] = i to load()
                }
                if (i % 1000000 == 0) {
                    i.println()
                    dump()
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val board = Board(input)
        board.slideNorth()
        board.dump()
        return board.load()
    }

    fun part2(input: List<String>): Int {
        val board = Board(input)
        board.cycle(1000000000)
        board.dump()
        return board.load()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 136)
    part2(testInput)

    val input = readInput("Day14")
    part1(input).println()
    // part2 has a cycle, so I'm going to find the right spot manually
    part2(input)
}