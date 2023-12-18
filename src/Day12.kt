import java.util.LinkedList

fun main() {
    data class Row(val line: String) {
        val arrangement = line.split(" ")[0]
        val spec = line.split(" ")[1].split(",").map { it.toInt() }
        val size = arrangement.length

        fun unfold(): Row {
            val arrs = arrayOf(arrangement, arrangement, arrangement, arrangement, arrangement)
            val specs = ArrayList<Int>()
            specs.addAll(spec)
            specs.addAll(spec)
            specs.addAll(spec)
            specs.addAll(spec)
            specs.addAll(spec)
            return Row(arrs.joinToString("?").plus(" ").plus(specs.joinToString(",")))
        }

        fun satisfied(maybe: String): Boolean {
            return maybe.length == size &&
                maybe.filterIndexed { index, c -> c == arrangement[index] || arrangement[index] == '?' }.length == size
        }

        fun partial(maybe: String): Boolean {
            if (maybe.length > size) {
                return false
            }
            return maybe.filterIndexed { index, c -> c == arrangement[index] || arrangement[index] == '?' }.length == maybe.length
        }

        fun createOption(operational: List<Int>, damaged: List<Int>): String {
            val sb = StringBuilder()
            for (i in operational.indices) {
                sb.append(".".repeat(operational[i]))
                if (i < damaged.size) {
                    sb.append("#".repeat(damaged[i]))
                }
            }
            return sb.toString()
        }

        fun options(): Set<String> {
            val todo = LinkedList<MutableList<Int>>()
            for (beg in 0.. size) {
                todo.add(mutableListOf(beg))
            }
            while (todo.isNotEmpty() && todo.peekFirst().size < spec.size) {
                val current = todo.pop()
                val left = size - spec.sum() - current.sum()
                val required = LinkedList<Int>()
                required.addAll(current)
                required.add(1)
                todo.add(required)
                for (i in 2..left) {
                    val next = LinkedList<Int>()
                    next.addAll(current)
                    next.add(i)
                    if (partial(createOption(next, spec))) {
                        todo.add(next)
                    }
                }
            }
            return todo.map { option ->
                val gap = size - option.sum() - spec.sum()
                if (gap > 0) {
                    option.add(gap)
                }
                option
            }.map { createOption(it, spec) }.toSet()
        }

        fun options2(): Set<String> {
            val todo = LinkedList<String>()
            var generation = 0
            val nextGen = LinkedList<String>()
            for (beg in 0.. size) {
                val next = ".".repeat(beg).plus("#".repeat(spec[generation]))
                if (partial(next)) {
                    nextGen.add(next)
                }
            }
            generation++
            while (generation < spec.size) {
                while (todo.isNotEmpty() && todo.peekFirst().length < size) {
                    val current = todo.pop()
                    val left = size - current.length
                    nextGen.add(current.plus(".").plus("#".repeat(spec[generation])))
                    for (i in 2..left) {
                        val next = current.plus(".".repeat(i)).plus("#".repeat(spec[generation]))
                        if (partial(next)) {
                            nextGen.add(next)
                        }
                    }
                }
                todo.addAll(nextGen)
                nextGen.clear()
                generation++
//                todo.println()
            }
            return todo.map { option ->
                val gap = size - option.length
                if (gap > 0) {
                    option.plus(".".repeat(gap))
                } else {
                    option
                }
            }.toSet()
        }
    }

    fun part1(input: List<String>): Int {
        return input.map { Row(it) }
            .map { row -> row.options().filter { row.satisfied(it) } }
            .sumOf{ it.size }
    }

    fun part2(input: List<String>): Int {
        val good = input.map { Row(it).unfold() }
            .map { row -> row.options().filter { row.satisfied(it) }.size
            }
        good.println()
        return good.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 525152)

    val input = readInput("Day12")
    part1(input).println()
//    part2(input).println()
}