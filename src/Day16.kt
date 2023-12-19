import java.util.HashMap
import java.util.LinkedList

enum class Dir {
    UP, RIGHT, DOWN, LEFT
}

fun main() {

    data class Beam(var pos: Pair<Int, Int>, var dir: Dir)
    data class Grid(val lines: List<String>) {
        fun parse(): MutableMap<Pair<Int, Int>, Char> {
            val m = HashMap<Pair<Int, Int>, Char>()
            for (y in lines.indices) {
                val row = lines[y]
                for (x in row.indices) {
                    m[y to x] = row[x]
                }
            }
            return m
        }

        val grid = parse()
        val beams = HashSet<Beam>()

        fun energized(): Int {
            return beams.map { it.pos }.toSet().size
        }

        fun dump() {
            val sb = StringBuilder()
            for (y in lines.indices) {
                val row = lines[y]
                for (x in row.indices) {
                    if (grid.containsKey(y to x)) {
                        if (grid[y to x] == '.') {
                            val found = beams.filter { it.pos == y to x }
                            if (found.isEmpty()) {
                                sb.append('.')
                            } else if (found.size > 1) {
                                sb.append(found.size)
                            } else {
                                sb.append(when (found[0].dir) {
                                    Dir.UP -> '^'
                                    Dir.RIGHT -> '>'
                                    Dir.DOWN -> 'v'
                                    Dir.LEFT -> '<'
                                })
                            }
                        } else {
                            sb.append(grid[y to x])
                        }
                    }
                }
                sb.append('\n')
            }
            sb.append(energized())
            sb.toString().println()
        }

        fun shoot(beam: Beam): List<Beam> {
            if (beams.contains(beam)) {
                return listOf()
            }
            if (beam.pos.first >= 0 && beam.pos.first < lines[0].length &&
                beam.pos.second >= 0 && beam.pos.second < lines.size
            ) {
                beams.add(beam)
            }

            val next = when (beam.dir) {
                Dir.UP -> beam.pos.first - 1 to beam.pos.second
                Dir.RIGHT -> beam.pos.first to beam.pos.second + 1
                Dir.DOWN -> beam.pos.first + 1 to beam.pos.second
                Dir.LEFT -> beam.pos.first to beam.pos.second - 1
            }
            if (next.first < 0 || next.first > lines[0].length ||
                next.second < 0 || next.second > lines.size
            ) {
                return listOf()
            }

            return when (grid[next]) {
                '.' -> listOf(Beam(next, beam.dir))
                '-' -> if (beam.dir == Dir.RIGHT || beam.dir == Dir.LEFT) {
                    listOf(Beam(next, beam.dir))
                } else {
                    listOf(Beam(next, Dir.LEFT), Beam(next, Dir.RIGHT))
                }

                '|' -> if (beam.dir == Dir.UP || beam.dir == Dir.DOWN) {
                    listOf(Beam(next, beam.dir))
                } else {
                    listOf(Beam(next, Dir.UP), Beam(next, Dir.DOWN))
                }

                '/' -> when (beam.dir) {
                    Dir.UP -> listOf(Beam(next, Dir.RIGHT))
                    Dir.RIGHT -> listOf(Beam(next, Dir.UP))
                    Dir.DOWN -> listOf(Beam(next, Dir.LEFT))
                    Dir.LEFT -> listOf(Beam(next, Dir.DOWN))
                }

                '\\' -> when (beam.dir) {
                    Dir.UP -> listOf(Beam(next, Dir.LEFT))
                    Dir.RIGHT -> listOf(Beam(next, Dir.DOWN))
                    Dir.DOWN -> listOf(Beam(next, Dir.RIGHT))
                    Dir.LEFT -> listOf(Beam(next, Dir.UP))
                }

                else -> listOf()
            }
        }
    }

    fun part1(input: List<String>): Int {
        val grid = Grid(input)
//        val beams = LinkedList<Beam>()
//        beams.add(Beam(0 to -1, Dir.RIGHT))
//        while (beams.isNotEmpty()) {
//            val beam = beams.pop()
//            val next = grid.shoot(beam)
//            beams.addAll(next)
//        }
//        grid.dump()
//        return grid.energized()
        val starting = LinkedList<Beam>()
        starting.add(Beam(0 to -1, Dir.RIGHT))
        return starting.maxOf {
            val beams = LinkedList<Beam>()
            beams.add(it)
            while (beams.isNotEmpty()) {
                val beam = beams.pop()
                val next = grid.shoot(beam)
                beams.addAll(next)
            }
            grid.energized()
        }
    }

    fun part2(input: List<String>): Int {
        val starting = LinkedList<Beam>()
        for (y in input.indices) {
            starting.add(Beam(y to -1, Dir.RIGHT))
            starting.add(Beam(y to input[0].length, Dir.LEFT))
        }
        for (x in input[0].indices) {
            starting.add(Beam(-1 to x, Dir.DOWN))
            starting.add(Beam(input.size to x, Dir.UP))
        }

        return starting.maxOf {
            val grid = Grid(input)
            val beams = LinkedList<Beam>()
            beams.add(it)
            while (beams.isNotEmpty()) {
                val beam = beams.pop()
                val next = grid.shoot(beam)
                beams.addAll(next)
            }
            grid.energized()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 46)

    val input = readInput("Day16")
    part1(input).println()

    check(part2(testInput) == 51)
    part2(input).println()
}