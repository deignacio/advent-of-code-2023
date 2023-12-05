fun main() {
    data class TranslationRule(val destStart: Long, val srcStart: Long, val length: Long) {
        val offset = destStart - srcStart
    }
    data class Translation(val dest: String, val src: String, val entries: List<TranslationRule>)

    fun translate(translation: Translation, srcValue: Long): Long {
        val rule = translation.entries.find { srcValue >= it.srcStart && srcValue < it.srcStart + it.length }
        if (rule != null) {
            return srcValue + rule.offset
        }
        return srcValue
    }

    fun applyRules(rules: Map<String, Translation>, src: String, value: Long, target: String): Long {
        var state = src
        var current = value
        while (!state.contentEquals(target)) {
            val rule = rules[state]!!
            current = translate(rule, current)
            state = rule.dest
        }
        return current
    }

    fun parseMap(line: String): Translation {
        val rows = line.split("\n")
        val keys = rows[0].split(" ")[0].split("-")
        val rules = rows.subList(1, rows.size).map {
            val rule = it.split(" ")
            TranslationRule(rule[0].toLong(), rule[1].toLong(), rule[2].toLong())
        }

        return Translation(keys[2], keys[0], rules)
    }

    fun parseMaps(input: List<String>): Map<String, Translation> {
        return input.joinToString("\n").split("\n\n")
            .map { parseMap(it) }
            .associateBy { it.src }
    }

    fun part1(input: List<String>): Long {
        fun parseSeeds(line: String): List<Long> {
            return line.split(": ")[1].split(" ").map { it.toLong() }
        }
        val seeds = parseSeeds(input[0])
        val translations = parseMaps(input.subList(2, input.size))
        val locations = seeds.map { applyRules(translations, "seed", it, "location") }
        return locations.min()
    }

    fun part2(input: List<String>): Long {
        data class SeedRange(val start: Long, val length: Long)
        fun parseSeeds(line: String): List<SeedRange> {
            val numbers = line.split(": ")[1].split(" ").map { it.toLong() }
            val ranges = ArrayList<SeedRange>()
            var i = 0
            while (i < numbers.size) {
                ranges.add(SeedRange(numbers[i], numbers[i + 1]))
                i += 2
            }
            return ranges
        }

        val ranges = parseSeeds(input[0])
        val translations = parseMaps(input.subList(2, input.size))
        var currentMin = Long.MAX_VALUE
        ranges.forEach {
            for (i in it.start..<it.start + it.length) {
                val current = applyRules(translations, "seed", i, "location")
                if (current < currentMin) {
                    currentMin = current
                }
            }
            // If you're actually brute forcing the solution, put these in to help give you an idea of progress
            // it.println()
            // println(currentMin)
        }
        return currentMin
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}