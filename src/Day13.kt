import kotlin.math.abs
import kotlin.math.min
fun main() {
    data class Desert(val tiles: Map<Pair<Int, Int>, Char>) {
        val width = tiles.keys.maxOf { it.second }
        val height = tiles.keys.maxOf { it.first }
        fun reflectVertical(axis: Int): Int {
            var errors = 0
            for (i in 0..min(axis-1, abs(width - axis))) {
                val x = axis - 1 - i
                val refX = axis + i
                for (y in 0..height) {
                    if (tiles[y to x] != tiles[y to refX]) {
                        errors ++
                    }
                }
            }
            return errors
        }
        fun reflectHorizontal(axis: Int): Int {
            var errors = 0
            for (i in 0..min(axis-1, abs(height - axis))) {
                val y = axis - 1 - i
                val refY = axis + i
                for (x in 0..width) {
                    if (tiles[y to x] != tiles[refY to x]) {
                        errors ++
                    }
                }
            }
            return errors
        }

        fun findMirror(errors: Int): Pair<Int, Int> {
            for (x in 1..width) {
                if (reflectVertical(x) == errors) {
                    return -1 to x
                }
            }
            for (y in 1..height) {
                if (reflectHorizontal(y) == errors) {
                    return y to -1
                }
            }
            return -1 to -1
        }
    }

    fun createDesert(lines: List<String>): Desert {
        val m = HashMap<Pair<Int, Int>, Char>()
        for (y in lines.indices) {
            val line = lines[y]
            for (x in line.indices) {
                m[y to x] = line[x]
            }
        }
        return Desert(m)
    }

    fun part1(input: List<String>): Int {
        return input.joinToString("\n").split("\n\n")
            .map { lines -> createDesert(lines.split("\n")) }
            .map { desert -> desert.findMirror(0) }
            .sumOf {
                if (it.first == -1) {
                    it.second
                } else if (it.second == -1){
                    100 * it.first
                } else {
                    "no reflection?".println()
                    0
                }
            }
    }

    fun part2(input: List<String>): Int {
        return input.joinToString("\n").split("\n\n")
            .map { lines -> createDesert(lines.split("\n")) }
            .map { desert -> desert.findMirror(1) }
            .sumOf {
                if (it.first == -1) {
                    it.second
                } else if (it.second == -1){
                    100 * it.first
                } else {
                    "no reflection?".println()
                    0
                }
            }
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 405)
    check(part2(testInput) == 400)

    val input = readInput("Day13")
    part1(input).println()
    part2(input).println()
}