import java.util.*
import kotlin.math.abs

fun main() {
    fun parse(input: List<String>): Set<Pair<Int, Int>> {
        return input.flatMapIndexed { y: Int, line:String ->
            line.mapIndexed{ x, c ->
                if (c == '#') {
                    y to x
                } else {
                    null
                }
            }.filterNotNull()
        }.toSet()
    }
    fun expand(input: List<String>, pts: Set<Pair<Int, Int>>, factor: Int): Set<Pair<Int, Int>> {
        val emptyRows = input.mapIndexedNotNull {
            y, line ->
            if (line.contains('#')) {
                null
            } else {
                y
            }
        }
        val emptyCols = ArrayList<Int>()
        for (x in input[0].indices) {
            if (input.indices.all {
                y -> input[y][x] == '.'
            }) {
                emptyCols.add(x)
            }
        }
        return pts.map { pt ->
            pt.first + (factor-1) * emptyRows.filter { it < pt.first }.size to
                    pt.second + (factor-1) * emptyCols.filter { it < pt.second }.size
        }.toSet()
    }

    fun distance(a: Pair<Int, Int>, b: Pair<Int, Int>): Long {
        return abs(a.first - b.first).toLong() + abs(a.second - b.second).toLong()
    }

    fun pairs (pts: Set<Pair<Int, Int>>): Set<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
        return pts.flatMap { a -> pts.map { b -> a to b } }.toSet()
    }

    fun part1(input: List<String>, factor: Int): Long {
        val pts = parse(input)
        val expanded = expand(input, pts,  factor)
        return pairs(expanded).sumOf { pair -> distance(pair.first, pair.second) } / 2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput, 2) == 374L)

    val input = readInput("Day11")
    part1(input, 2).println()

    check(part1(testInput, 10) == 1030L)
    check(part1(testInput, 100) == 8410L)
    part1(input, 1000000).println()
}