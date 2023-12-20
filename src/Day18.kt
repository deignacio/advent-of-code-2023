fun main() {

    data class Instruction(val dir: Dir, val dist: Int, val color: String)

    class Lagoon(var pos: Pair<Int, Int>) {
        val beg = pos.first to pos.second
        val pts = HashMap<Pair<Int, Int>, String>()

        fun dump() {
            val sb = StringBuilder()
            val minh = pts.keys.minOf { it.first }
            val h = pts.keys.maxOf { it.first }
            val minw = pts.keys.minOf { it.second }
            val w = pts.keys.maxOf { it.second }
            for (y in minh..h) {
                for (x in minw..w) {
                    if (beg == y to x) {
                        sb.append('X')
                    } else {
                        sb.append(
                            when (pts[y to x]) {
                                null -> '.'
                                else -> '#'
                            }
                        )
                    }
                }
                sb.append('\n')
            }
            sb.toString().println()
        }
        fun paint(dir: Dir, color: String) {
            pos = when (dir) {
                Dir.UP -> pos.first - 1 to pos.second
                Dir.RIGHT -> pos.first to pos.second + 1
                Dir.DOWN -> pos.first + 1 to pos.second
                Dir.LEFT -> pos.first to pos.second - 1
            }
            pts[pos] = color
        }
        fun dig(instructions: List<Instruction>) {
            instructions.forEach {
                for (i in 1..it.dist) {
                    paint(it.dir!!, it.color)
                }
            }
        }

        fun fill(from: Pair<Int, Int>, color: String) {
            pts[from] = color
            val minh = pts.keys.minOf { it.first }
            val h = pts.keys.maxOf { it.first }
            val minw = pts.keys.minOf { it.second }
            val w = pts.keys.maxOf { it.second }
            var changed = true
            while (changed) {
                changed = false
                for (y in minh..h) {
                    for (x in minw..w) {
                        if (pts[y to x] == null) {
                            if (pts[y - 1 to x] == color ||
                                pts[y + 1 to x] == color ||
                                pts[y to x - 1] == color ||
                                pts[y to x + 1] == color
                            ) {
                               pts[y to x] = color
                               changed = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val l = Lagoon(0 to 0)
        l.pts[0 to 0] = "start"
        l.dig(input.map {
            val dir = when (it.split(' ')[0]) {
                "U" -> Dir.UP
                "R" -> Dir.RIGHT
                "D" -> Dir.DOWN
                "L" -> Dir.LEFT
                else -> null
            }
            val dist = it.split(' ')[1].toInt()
            val color = it.split(' ')[2].substring(1, 8)
            Instruction(dir!!, dist, color)
        })
//        l.dump()
        l.fill(1 to 1, "lava")
//        l.dump()
        return l.pts.keys.size
    }

    fun part2(input: List<String>): Int {
        val l = Lagoon(0 to 0)
        l.pts[0 to 0] = "start"
        l.dig(input.map {
            val i = it.split(' ')[2].substring(1, 8)
            val dist = i.substring(1, 5).toInt(16)
            val dir = when (i.get(6)) {
                '0' -> Dir.RIGHT
                '1' -> Dir.DOWN
                '2' -> Dir.LEFT
                '3' -> Dir.UP
                else -> null
            }
            Instruction(dir!!, dist, "ignored")
        })
        l.dump()
//        l.fill(1 to 1, "lava")
//        l.dump()
        return l.pts.keys.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 62)

    val input = readInput("Day18")
    part1(input).println()
//    part2(input).println()
}