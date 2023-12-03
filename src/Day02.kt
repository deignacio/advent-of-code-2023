import kotlin.math.max

fun main() {
    data class Game(val id: Int, val moves: List<Map<String, Int>>)

    fun parseLine(input: String): Game {
        val id = input.substringBefore(": ").substringAfter(" ").toInt()
        val moveList = input.substringAfter(": ")
        val moves = moveList.split("; ").map {
            s: String ->
            s.split(", ").associate {
                it.substringAfter(" ") to it.substringBefore(" ").toInt()
            }
        }
        return Game(id, moves)
    }


    fun part1(input: List<String>): Int {
        val constraints = mapOf("red" to 12, "green" to 13, "blue" to 14)
        fun isValidGame(game: Game, constraints: Map<String, Int>): Boolean {
            val invalid = game.moves.indexOfFirst {
                moves -> moves.any {
                    entry -> entry.value > constraints.getValue(entry.key)
                }
            }
            return invalid < 0
        }
        fun scoreLine(line: String): Int {
            val game = parseLine(line)
            return if (isValidGame(game, constraints)) {
                game.id
            } else {
                0
            }
        }
        return input.sumOf {
            s -> scoreLine(s)
        }
    }

    fun part2(input: List<String>): Int {
        fun findPower(line: String): Int {
            val game = parseLine(line)
            val reduced = game.moves.reduce{
                first, second -> mapOf(
                    "red" to max(first.getOrDefault("red", 0), second.getOrDefault("red", 0)),
                    "green" to max(first.getOrDefault("green", 0), second.getOrDefault("green", 0)),
                    "blue" to max(first.getOrDefault("blue", 0), second.getOrDefault("blue", 0))
                )
            }
            return reduced.getOrDefault("red", 0) *
                reduced.getOrDefault("green", 0) *
                reduced.getOrDefault("blue", 0)
        }
        return input.sumOf {
            s -> findPower(s)
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}