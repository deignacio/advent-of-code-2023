import java.util.*

fun main() {
    data class Cart(var pos: Pair<Int, Int>, var dir: Dir) {
        fun goStraight() {
            pos = when (dir) {
                Dir.UP -> pos.first - 1 to pos.second
                Dir.RIGHT -> pos.first to pos.second + 1
                Dir.DOWN -> pos.first + 1 to pos.second
                Dir.LEFT -> pos.first to pos.second - 1
            }
        }

        fun turnLeft() {
            dir = when (dir) {
                Dir.UP -> Dir.LEFT
                Dir.RIGHT -> Dir.UP
                Dir.DOWN -> Dir.RIGHT
                Dir.LEFT -> Dir.DOWN
            }
        }

        fun turnRight() {
            dir = when (dir) {
                Dir.UP -> Dir.RIGHT
                Dir.RIGHT -> Dir.DOWN
                Dir.DOWN -> Dir.LEFT
                Dir.LEFT -> Dir.UP
            }
        }

        fun copy(): Cart {
            return Cart(pos, dir)
        }
    }

    data class Trail(val current: Cart, var cost: Int = 0, var momentum: Int = 0) {

        fun key(): Pair<Cart, Int> {
            return current to momentum
        }

        fun copy(): Trail {
            return Trail(Cart(current.pos, current.dir), cost, momentum)
        }

        fun goStraight(): Trail {
            val next = copy()
            next.current.goStraight()
            next.momentum++
            return next
        }

        fun goLeft():Trail {
            val next = copy()
            next.current.turnLeft()
            next.momentum = 1
            next.current.goStraight()
            return next
        }

        fun goRight():Trail {
            val next = copy()
            next.current.turnRight()
            next.momentum = 1
            next.current.goStraight()
            return next
        }
    }

    class Path() {
        val trail = LinkedList<Cart>()

        fun key(): Pair<Cart, Int> {
            return trail.last() to momentum()
        }

        fun momentum(): Int {
            if (trail.size < 3) {
                return trail.size
            }
            val last = trail.subList(trail.size - 3, trail.size).map { it.dir }
            return if (last[2] == last[1]) {
                if (last[1] == last[0]) {
                    3
                } else {
                    2
                }
            } else {
                1
            }
        }

        fun add(cart: Cart) {
            trail.add(cart)
        }

        fun copy(): Path {
            val p = Path()
            p.trail.addAll(trail)
            return p
        }

        fun goStraight(): Path {
            val next = trail.last().copy()
            next.goStraight()
            add(next)
            return this
        }

        fun goLeft(): Path {
            val next = trail.last().copy()
            next.turnLeft()
            next.goStraight()
            add(next)
            return this
        }

        fun goRight(): Path {
            val next = trail.last().copy()
            next.turnRight()
            next.goStraight()
            add(next)
            return this
        }
    }

    data class Grid(val lines: List<String>) {
        fun parse(): MutableMap<Pair<Int, Int>, Int> {
            val m = HashMap<Pair<Int, Int>, Int>()
            for (y in lines.indices) {
                val row = lines[y]
                for (x in row.indices) {
                    m[y to x] = row[x].code - 48 // ascii digits '0' -> 48
                }
            }
            return m
        }

        val grid = parse()
        val end = lines.size - 1 to lines[0].length - 1
        val cache = HashMap<Pair<Cart, Int>, Int>()

        fun cost(path: Path): Int {
            return path.trail.subList(1, path.trail.size)
                .map { it.pos }
                .sumOf { grid[it]!! }
        }

        fun dump(path: Path) {
            val sb = StringBuilder()
            sb.append(path.trail.last())
            sb.append('\n')
            for (y in lines.indices) {
                val row = lines[y]
                for (x in row.indices) {
                    if (grid.containsKey(y to x)) {
                        val found = path.trail.filter { it.pos == y to x }
                        if (found.isEmpty()) {
                            sb.append(grid[y to x])
                        } else if (found.size > 1) {
                            sb.append(found.size)
                        } else {
                            sb.append(when (found[0].dir) {
                                Dir.UP -> '^'
                                Dir.RIGHT -> '>'
                                Dir.DOWN -> 'v'
                                Dir.LEFT -> '<'
                            })
                        }
                    }
                }
                sb.append('\n')
            }
            sb.append(cost(path))
            sb.toString().println()
        }

        fun inBounds(path: Path): Boolean {
            return inBounds(path.trail.last())
        }

        fun inBounds(cart: Cart): Boolean {
//            cart.println()
            return cart.pos.first >= 0 && cart.pos.first < lines.size &&
                    cart.pos.second >= 0 && cart.pos.second < lines[0].length
        }

        fun ride(trail: Trail): List<Trail> {
            if (trail.current.pos == end) {
                return listOf()
            }

            val next = ArrayList<Trail>()
            if (trail.momentum < 3) {
                next.add(trail.goStraight())
            }
            next.add(trail.goLeft())
            next.add(trail.goRight())
            return next.filter { inBounds(it.current) }
                .map {
                    it.cost += grid[it.current.pos]!!
                    it
                }
                .filter { !cache.containsKey(it.key()) || it.cost < cache[it.key()]!! }
                .map {
                    cache[it.key()] = it.cost
                    it
                }
        }

        fun rideUltra(trail: Trail): List<Trail> {
            if (trail.current.pos == end) {
                return listOf()
            }

            val next = ArrayList<Trail>()
            if (trail.momentum < 4) {
                next.add(trail.goStraight())
            } else {
                if (trail.momentum < 10) {
                    next.add(trail.goStraight())
                }
                next.add(trail.goLeft())
                next.add(trail.goRight())
            }
            return next.filter { inBounds(it.current) }
                .map {
                    it.cost += grid[it.current.pos]!!
                    it
                }
                .filter { !cache.containsKey(it.key()) || it.cost < cache[it.key()]!! }
                .map {
                    cache[it.key()] = it.cost
                    it
                }
        }

        fun ride(path: Path): List<Path> {
            if (path.trail.last().pos == end) {
                return listOf()
            }

            val next = ArrayList<Path>()
            // go straight
            if (path.momentum() < 3) {
                next.add(path.copy().goStraight())
            }
            next.add(path.copy().goLeft())
            next.add(path.copy().goRight())
            return next.filter { inBounds(it) }
                .filter { !cache.containsKey(it.key()) || cost(it) < cache[it.key()]!! }
                .map {
                    cache[it.key()] = cost(it)
                    it
                }
        }
    }

    fun part1(input: List<String>): Int {
        val grid = Grid(input)
        val starting = LinkedList<Cart>()
        starting.add(Cart(0 to 0, Dir.RIGHT))
        starting.add(Cart(0 to 0, Dir.DOWN))
        starting.map {
            val p = Path()
            p.add(it)
            val paths = LinkedList<Path>()
            paths.add(p)
            while (paths.isNotEmpty()) {
                paths.addAll(grid.ride(paths.pop()))
            }
        }
        return grid.cache.keys
            .filter { it.first.pos == grid.end }
            .minOf { grid.cache[it]!! }
    }

    fun part1Trails(input: List<String>): Int {
        val grid = Grid(input)
        val starting = LinkedList<Cart>()
        starting.add(Cart(0 to 0, Dir.RIGHT))
        starting.add(Cart(0 to 0, Dir.DOWN))
        starting.map {
            val trails = LinkedList<Trail>()
            trails.add(Trail(it, 0, 0))
            while (trails.isNotEmpty()) {
                trails.addAll(grid.ride(trails.pop()))
            }
        }
        return grid.cache.keys
            .filter { it.first.pos == grid.end }
            .minOf { grid.cache[it]!! }
    }

    fun part2(input: List<String>): Int {
        val grid = Grid(input)
        val starting = LinkedList<Cart>()
        starting.add(Cart(0 to 0, Dir.RIGHT))
        starting.add(Cart(0 to 0, Dir.DOWN))
        starting.map {
            val trails = LinkedList<Trail>()
            trails.add(Trail(it, 0, 0))
            while (trails.isNotEmpty()) {
                trails.addAll(grid.rideUltra(trails.pop()))
            }
        }
        return grid.cache.keys
            .filter { it.first.pos == grid.end && it.second >= 4 }
            .minOf { grid.cache[it]!! }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 102)
    check(part1Trails(testInput) == 102)
    check(part2(testInput) == 94)

    val input = readInput("Day17")
    part1Trails(input).println()
    part2(input).println()
}