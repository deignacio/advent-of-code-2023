import java.util.*
import kotlin.collections.HashMap

fun main() {

    fun parseObj(raw: String): Map<String, Int> {
        val attrs = raw.split('{')[1].split('}')[0].split(',')
        val obj = HashMap<String, Int>()
        obj["x"] = attrs.filter { it.contains('x') }[0].split('=')[1].toInt()
        obj["m"] = attrs.filter { it.contains('m') }[0].split('=')[1].toInt()
        obj["a"] = attrs.filter { it.contains('a') }[0].split('=')[1].toInt()
        obj["s"] = attrs.filter { it.contains('s') }[0].split('=')[1].toInt()
        return obj
    }
    data class Rule(val attr: String?, val op: Char?, val arg: Int?, val target: String) {
        fun apply(obj: Map<String, Int>): String? {
            if (attr == null) {
                return target
            }
            return when (op!!) {
                '>' -> if (obj[attr]!! > arg!!) {
                        target
                    } else {
                        null
                    }
                '<' -> if (obj[attr]!! < arg!!) {
                        target
                    } else {
                        null
                    }
                else -> null
            }
        }
    }

    fun parseRule(raw: String): Rule {
        if (raw.contains(":")) {
            val queryRaw = raw.split(':')[0]
            val op = queryRaw.findAnyOf(listOf("<", ">"))!!.second[0]
            val attr = queryRaw.split(op)[0]
            val arg = queryRaw.split(op)[1].toInt()
            val target = raw.split(':')[1]
            return Rule(attr, op, arg, target)
        }
        return Rule(null, null, null, raw)
    }

    data class Workflow(val line: String) {
        val name = line.split('{')[0]
        val rules = line.split('{')[1].split('}')[0].split(',')
            .map { parseRule(it) }

        fun apply(obj: Map<String, Int>): String {
            return rules.firstNotNullOf { it.apply(obj) }
        }
    }

    fun part1(input: List<String>): Int {
        val lines = input.joinToString("\n").split("\n\n")
        val workflows = lines[0].split('\n')
            .map { Workflow(it) }
            .associateBy { it.name }

        return lines[1].split('\n').map { parseObj(it) }
            .map {
                var next = "in"
                while (next != "R" && next != "A") {
                    next = workflows[next]!!.apply(it)
                }
                it to next
            }
            .filter { it.second == "A" }
            .sumOf { it.first.values.sum() }
    }

    class Route {
        val steps = ArrayList<Pair<String, Int>>()

        fun merge(workflows: Map<String, Workflow>): Long {
            val merged = HashMap<String, MutableList<Pair<Rule, Boolean>>>()
            steps.flatMap { it: Pair<String, Int> ->
                    val rules = ArrayList<Pair<Rule, Boolean>>()
                    for (i in 0..<it.second) {
                        rules.add(workflows[it.first]!!.rules[i] to false)
                    }
                    rules.add(workflows[it.first]!!.rules[it.second] to true)
                    rules
                }
                .filter { it.first.attr != null }
                .forEach {
                    if (!merged.containsKey(it.first.attr)) {
                        merged[it.first.attr!!] = ArrayList()
                    }
                    merged[it.first.attr!!]!!.add(it)
                }
            val asIntervals = merged.entries.associate {
                    it.key to it.value.map { rule ->
                        when (rule.first.op!!) {
                            '>' -> if (rule.second) {
                                (rule.first.arg!!+1)..4000
                            } else {
                                1..rule.first.arg!!
                            }
                            '<' -> if (rule.second) {
                                1..<rule.first.arg!!
                            } else {
                                rule.first.arg!!..4000
                            }
                            else -> 1..4000
                        }.toSet()
                    }.reduce { acc, interval -> acc.intersect(interval) }.size.toLong()
                }
            return asIntervals.values.reduce { acc, i -> acc * i } *
                    when (asIntervals.values.size) {
                        1 -> 4000 * 4000 * 4000L
                        2 -> 4000 * 4000L
                        3 -> 4000L
                        4 -> 1L
                        else -> 1L
                    }
        }

        fun addStep(name: String, rule: Int): Route {
            val r = Route()
            r.steps.addAll(steps)
            r.steps.add(name to rule)
            return r
        }
    }

    fun part2(input: List<String>): Long {
        val lines = input.joinToString("\n").split("\n\n")
        val workflows = lines[0].split('\n')
            .map { Workflow(it) }
            .associateBy { it.name }
        val accepted = LinkedList<Route>()
        val toProcess = LinkedList<Route>()
        for (i in workflows["in"]!!.rules.indices) {
            toProcess.add(Route().addStep("in", i))
        }
        while (toProcess.isNotEmpty()) {
            val next = toProcess.pop()
            val target = workflows[next.steps.last().first]!!.rules[next.steps.last().second].target
            if (target == "R") {
                continue
            } else if (target == "A") {
                accepted.add(next)
                continue
            }
            for (i in workflows[target]!!.rules.indices) {
                toProcess.add(next.addStep(target, i))
            }
        }
        return accepted.sumOf { it.merge(workflows) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 19114)
    check(part2(testInput) == 167409079868000L)
    val input = readInput("Day19")
    part1(input).println()
    part2(input).println()
}