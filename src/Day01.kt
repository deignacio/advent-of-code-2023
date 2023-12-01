fun main() {
    val digits = listOf( "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" )

    fun part1(input: List<String>): Int {
        fun calibrate(str: String): Int {
            val front = str.first{ c -> c.isDigit() }
            val back = str.last { c -> c.isDigit() }
            return "".plus(front).plus(back).toInt()
        }
        return input.sumOf { s -> calibrate(s) }
    }

    fun part2(input: List<String>): Int {
        fun calibrate(str: String): Int {
            val frontDigitIndex = str.indexOfFirst{ c -> c.isDigit() }
            val frontWordPair = str.findAnyOf(digits)
            var front = 0
            if (frontDigitIndex >= 0) {
                front = str[frontDigitIndex].digitToInt()
            }
            if (frontWordPair != null && (frontDigitIndex == -1 || frontWordPair.first < frontDigitIndex)) {
                front = digits.indexOf(frontWordPair.second) + 1
            }

            val backDigitIndex = str.indexOfLast { c -> c.isDigit() }
            val backWordPair = str.findLastAnyOf(digits)
            var back = 0
            if (backDigitIndex >= 0) {
                back = str[backDigitIndex].digitToInt()
            }
            if (backWordPair != null && (backDigitIndex == -1 || backWordPair.first > backDigitIndex)) {
                back = digits.indexOf(backWordPair.second) + 1
            }

            return "".plus(front).plus(back).toInt()
        }
        return input.sumOf { s -> calibrate(s) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)
    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
