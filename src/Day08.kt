fun main() {
    data class Node(val name: String, val left: String, val right: String)

    fun parseNode(line: String): Node {
        return Node(line.substring(0, 3), line.substring(7, 10), line.substring(12, 15))
    }

    fun part1(input: List<String>): Int {
        val circuit = input[0]
        val nodes = input.subList(2, input.size).map { parseNode(it) }.associateBy { it.name }
        var current = "AAA"
        var count = 0
        while (!current.contentEquals("ZZZ")) {
            current = if (circuit[count % circuit.length] == 'L') {
                nodes[current]!!.left
            } else {
                nodes[current]!!.right
            }
            count++
        }
        return count
    }

    fun part2(input: List<String>): Set<Int> {
        val circuit = input[0]
        val nodes = input.subList(2, input.size).map { parseNode(it) }.associateBy { it.name }
        var current = nodes.filterKeys { it.endsWith("A") }.keys.toList()
        var count = 0
        // This is a weird way to initialize a map with indexes and 0s
        val foundPeriod = current.mapIndexed { index, s -> index to 0 }.toMap().toMutableMap()
        while (foundPeriod.any { it.value == 0 }) {
            current = if (circuit[count % circuit.length] == 'L') {
                current.map { nodes[it]!!.left }
            } else {
                current.map { nodes[it]!!.right }
            }
            count++
            if (current.any { it.endsWith("Z") }) {
                current.forEachIndexed { index, it ->
                    if (it.endsWith("Z")) {
                        foundPeriod[index] = count
                    }
                }
            }
        }
        return foundPeriod.values.toSet()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 2)
    val testInput2 = readInput("Day08_test2")
    check(part1(testInput2) == 6)
    val testInput3 = readInput("Day08_test3")
    part2(testInput3).println()

    val input = readInput("Day08")
    part1(input).println()
    // This is needed to run to find periods. All other stuff was done in GCD/LCM calculations outside of this file
    part2(input).println()
}