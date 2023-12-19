fun main() {

    data class Lens(val label: String, var focal: Int)
    data class Operation(val cmd: String) {
        val label: String = cmd.split("-")[0].split("=")[0]
        val op: Char = if (cmd.contains("-")) {
            '-'
        } else {
            '='
        }
        val focal: Int = if (cmd.contains("=")) {
            cmd.split("=")[1].toInt()
        } else {
            -1
        }
    }

    fun hash(line: String): Int {
        var current = 0
        for (c in line) {
            current += c.code
            current *= 17
            current %= 256
        }
        return current
    }

    fun part1(input: List<String>): Int {
        return input[0].split(",")
            .sumOf { hash(it) }
    }

    fun part2(input: List<String>): Int {
        val operations = input[0].split(",")
            .map { Operation(it) }
        val boxes = HashMap<Int, MutableList<Lens>>()
        for (op in operations) {
            val box = hash(op.label)
            if (!boxes.containsKey(box)) {
                boxes[box] = ArrayList()
            }
            if (op.op == '-') {
                boxes[box]!!.removeIf { it.label == op.label }
            } else {
                val lens = boxes[box]!!.find { it.label == op.label }
                if (lens != null) {
                    lens.focal = op.focal
                } else {
                    boxes[box]!!.add(Lens(op.label, op.focal))
                }
            }
        }
        return boxes.entries.sumOf {
            entry -> (entry.key + 1) * entry.value.mapIndexed { index, lens -> (index + 1) * lens.focal }.sum()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testHash = readInput("Day15_testHash")
    check(hash(testHash[0]) == 52)

    val testInput = readInput("Day15_test")
    check(part1(testInput) == 1320)
    check(part2(testInput) == 145)

    val input = readInput("Day15")
    part1(input).println()
    part2(input).println()
}