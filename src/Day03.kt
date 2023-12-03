import kotlin.math.abs

fun main() {
    data class Num(val raw: String, val value: Int, val x: Int, val y: Int)
    data class Symbol(val raw: Char, val x: Int, val y: Int)

    fun findNumbers(line: String, row: Int): List<Num> {
        val numbers = ArrayList<Num>()
        val parsed = line.split("\\p{Punct}".toRegex()).filter { it.isNotEmpty() }
        var idx = 0
        for (n in parsed) {
            idx = line.indexOf(n, idx)
            numbers.add(Num(n, n.toInt(), idx, row))
            idx += n.length
        }
        return numbers
    }

    fun overlaps(num: Num, symbol: Symbol): Boolean {
        if (abs(num.y - symbol.y) > 1) {
            return false
        }
        if (symbol.x < num.x - 1) {
            return false
        }
        if (symbol.x > num.x + num.raw.length) {
            return false
        }
        return true
    }

    fun overlapsRow(num: Num, symbols: List<Symbol>): Symbol? {
        for (s in symbols) {
            if (overlaps(num, s)) {
                return s
            }
        }
        return null
    }
    fun overlapsSymbol(num: Num, allSymbolsByRow: List<List<Symbol>>, row: Int): Symbol? {
        if (row > 0) {
            // check up
            val overlapped = overlapsRow(num, allSymbolsByRow[row - 1])
            if (overlapped != null) {
                return overlapped
            }
        }
        run {
            // check self
            val overlapped = overlapsRow(num, allSymbolsByRow[row])
            if (overlapped != null) {
                return overlapped
            }
        }
        if (row + 1 < allSymbolsByRow.size) {
            // check down
            val overlapped = overlapsRow(num, allSymbolsByRow[row + 1])
            if (overlapped != null) {
                return overlapped
            }
        }
        return null
    }

    fun findValidNumbers(allNumbersByRow: List<List<Num>>, allSymbolsByRow: List<List<Symbol>>): Map<Num, Symbol> {
        val valid = HashMap<Num, Symbol>()
        for (row in allNumbersByRow.indices) {
            allNumbersByRow[row].associateWith { overlapsSymbol(it, allSymbolsByRow, row) }
                .forEach {
                    if (it.value != null) {
                        valid[it.key] = it.value!!
                    }
                }
        }
        return valid
    }

    fun part1(input: List<String>): Int {
        fun findSymbols(line: String, row: Int): List<Symbol> {
            val symbols = ArrayList<Symbol>()
            val sanitized = line.replace(".", " ")
            val punct = "\\p{Punct}".toRegex()
            for (i in line.indices) {
                if (punct.matchesAt(sanitized, i)) {
                    symbols.add(Symbol(sanitized[i], i, row))
                }
            }
            return symbols
        }

        val allSymbols = input.mapIndexed { index, s -> findSymbols(s, index) }
        val allNumbers = input.mapIndexed { index, s -> findNumbers(s, index) }
        return findValidNumbers(allNumbers, allSymbols).keys.sumOf{ it.value }
    }

    fun part2(input: List<String>): Int {
        fun findSymbols(line: String, row: Int): List<Symbol> {
            val symbols = ArrayList<Symbol>()
            val sanitized = line.replace(".", " ")
            val punct = "\\*".toRegex()
            for (i in line.indices) {
                if (punct.matchesAt(sanitized, i)) {
                    symbols.add(Symbol(sanitized[i], i, row))
                }
            }
            return symbols
        }
        val allSymbols = input.mapIndexed { index, s -> findSymbols(s, index) }
        val allNumbers = input.mapIndexed { index, s -> findNumbers(s, index) }
        val valid = findValidNumbers(allNumbers, allSymbols)
        val gears = HashMap<Symbol, MutableList<Num>>()
        for (entry in valid) {
            if (!gears.containsKey(entry.value)) {
                gears[entry.value] = ArrayList()
            }
            gears[entry.value]!!.add(entry.key)
        }
        return gears.filterValues { it.size == 2 }.values
            .sumOf { it[0].value * it[1].value }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}