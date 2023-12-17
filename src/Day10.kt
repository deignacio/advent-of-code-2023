fun main() {
    data class Sketch(val input: List<String>) {
        fun parsePipes(): Map<Int, Map<Int, Char>> {
            return input.mapIndexed { rowNum, line ->
                rowNum to line.mapIndexed { colNum, c -> colNum to c }
                    .associate { pair -> pair.first to pair.second }
            }
                .associate { row -> row.first to row.second }
        }
        val pipes = parsePipes()

        fun findStart(): Pair<Int, Int> {
            val row = input.indexOfFirst { it.contains('S') }
            val col = input[row].indexOfFirst { it == 'S' }
            return row to col
        }
        val start = findStart()
        fun getVal(coord: Pair<Int, Int>): Char {
            return pipes[coord.first]!![coord.second]!!
        }

        fun getNorth(coord: Pair<Int, Int>): Pair<Int, Int> {
            return coord.first - 1 to coord.second
        }
        fun getSouth(coord: Pair<Int, Int>): Pair<Int, Int> {
            return coord.first + 1 to coord.second
        }
        fun getEast(coord: Pair<Int, Int>): Pair<Int, Int> {
            return coord.first to coord.second + 1
        }
        fun getWest(coord: Pair<Int, Int>): Pair<Int, Int> {
            return coord.first to coord.second - 1
        }
        fun getNext(prev: Pair<Int, Int>, current: Pair<Int, Int>): Pair<Int, Int> {
            val value = getVal(current)
            val north = getNorth(current)
            val south = getSouth(current)
            val east = getEast(current)
            val west = getWest(current)

            return when (value) {
                '|' -> if (prev == north) {
                    south
                } else {
                    north
                }
                '-' -> if (prev == west) {
                    east
                } else {
                    west
                }
                'L' -> if (prev == north) {
                    east
                } else {
                    north
                }
                'J' -> if (prev == north) {
                    west
                } else {
                    north
                }
                '7' -> if (prev == south) {
                    west
                } else {
                    south
                }
                'F' -> if (prev == south) {
                    east
                } else {
                    south
                }
                'S' -> {
                    if (getVal(north) == '|' || getVal(north) == 'F') {
                        return north
                    } else if (getVal(east) == '-' || getVal(east) == '7') {
                        return east
                    } else if (getVal(south) == '|' || getVal(south) == 'J') {
                        return south
                    } else if (getVal(west) == '-' || getVal(west) == 'L') {
                        return west
                    }
                    -1 to -1
                }
                else -> -1 to -1
            }
        }

        fun walk(): Map<Pair<Int, Int>, Int> {
            assert(getVal(start) == 'S')
            val distances = HashMap<Pair<Int, Int>, Int>()
            var distance = 0
            var prev: Pair<Int, Int> = start
            var current: Pair<Int, Int> = start
            var next: Pair<Int, Int>
            do {
                distances[current] = distance
                next = getNext(prev, current)
                prev = current
                current = next
                distance++
            } while (getVal(current) != 'S')
            return distances
        }
        val distances = walk()
        fun simplify(): String {
            val sb = StringBuilder()
            for (x in input[0].indices) {
                sb.append("..")
            }
            for (y in input.indices) {
                sb.append('\n')
                val line = input[y]
                for (x in line.indices) {
                    val value = distances[y to x]
                    if (value == null) {
                        sb.append("* ")
                    } else {
                        val current = getVal(y to x)
                        sb.append(
                            when (current) {
                                '|' -> "| "
                                '-' -> "--"
                                'L' -> "L-"
                                'J' -> "J "
                                'F' -> "F-"
                                '7' -> "7 "
                                'S' -> "SS"
                                else -> "  "
                            }
                        )
                    }
                }
                sb.append('\n')
                for (x in line.indices) {
                    val value = distances[y to x]
                    if (value == null) {
                        sb.append("  ")
                    } else {
                        val current = getVal(y to x)
                        sb.append(
                            when (current) {
                                '|' -> "| "
                                '-' -> "  "
                                'L' -> "  "
                                'J' -> "  "
                                'F' -> "| "
                                '7' -> "| "
                                'S' -> "S "
                                else -> "  "
                            }
                        )
                    }
                }
            }
            return sb.toString()
        }
        var simplified = simplify()
        fun prune() {
            var count = 1
            while (count > 0) {
                count = 0
                val sb = StringBuilder()
                val lines = simplified.split('\n')
                sb.append(lines[0])
                for (y in 1..<lines.size-1) {
                    sb.append('\n')
                    val line = lines[y]
                    for (x in line.indices) {
                        val cur = line[x]
                        if (cur == '*' || cur == ' ') {
                            if (x == 0 || line[x-1] == '.') {
                                sb.append('.')
                                count++
                            } else if (x == line.length-1 || line[x+1] == '.') {
                                sb.append('.')
                                count++
                            } else {
                                if (lines[y+1][x] == '.') {
                                    sb.append('.')
                                    count++
                                } else if (lines[y-1][x] == '.') {
                                    sb.append('.')
                                    count++
                                } else {
                                    sb.append(cur)
                                }
                            }
                        } else {
                            sb.append(cur)
                        }
                    }
                }
                sb.append('\n')
                sb.append(lines.last())
                simplified = sb.toString()
            }
        }
    }

    fun part1(input: List<String>): Int {
        val sketch = Sketch(input)
        return (sketch.distances.values.max() + 1) / 2
    }

    fun part2(input: List<String>): Int {
        val sketch = Sketch(input)
        sketch.prune()
        sketch.simplified.println()
        return sketch.simplified.count { c -> c == '*' }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 4)
    val testInput2 = readInput("Day10_test2")
    check(part1(testInput2) == 4)
    val testInput3 = readInput("Day10_test3")
    check(part1(testInput3) == 8)
    val testInput4 = readInput("Day10_test4")
    check(part1(testInput4) == 8)
    val testInput5 = readInput("Day10_test5")
    check(part2(testInput5) == 4)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}