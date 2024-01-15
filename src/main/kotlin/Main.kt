import kotlin.random.Random

fun makeArea(size: Int, countMine: Int): MutableList<MutableList<String>> {
    val area = MutableList(size) { MutableList(size) { "." } }
    val areaWithMine = MutableList(size) { MutableList(size) { "." } }
    println(makePresentableArea(area))
    println("Set/unset mines marks or claim a cell as free:")
    var (col, row, mod) = readln().split(" ")
    while (countMine != area.sumOf { it.filter { it == "X" }.size } || mod != "free") {
        if (mod == "mine") {
            if (areaWithMine[row.toInt() - 1][col.toInt() - 1] == "*") areaWithMine[row.toInt() - 1][col.toInt() - 1] =
                "."
            else areaWithMine[row.toInt() - 1][col.toInt() - 1] = "*"
            println(makePresentableArea(areaWithMine))
            println("Set/unset mines marks or claim a cell as free:")
            val (col1, row1, mod1) = readln().split(" ")
            col = col1
            row = row1
            mod = mod1
        } else {
            val mines = mutableListOf<Pair<Int, Int>>()
            while (mines.size < countMine) {
                val x = Random.nextInt(0, size)
                val y = Random.nextInt(0, size)
                val mine = Pair(x, y)
                if (!mines.contains(mine) && mine != Pair(row.toInt() - 1, col.toInt() - 1)) {
                    mines.add(mine)
                }
            }
            for (i in mines)
                area[i.first][i.second] = "X"
        }

        if (countMine == area.sumOf { it.filter { it == "X" }.size }) setLogic(
            area,
            areaWithMine,
            col.toInt(),
            row.toInt()
        )
    }

    return area
}

fun makePresentableArea(area: MutableList<MutableList<String>>): String {
    var i = 1
    return area.joinToString("\n", prefix = " │123456789│\n—│—————————│\n", postfix = "\n—│—————————│")
    { it.joinToString("", postfix = "|", prefix = "${i++}|") }
}

fun giveHelp(area: MutableList<MutableList<String>>): MutableList<MutableList<String>> {
    var row = 0
    var col = 0
    for (listEl in area) {
        for (el in listEl) {
            if (el == ".") {
                val list = mutableListOf<String>()
                for (i in row - 1..row + 1) {
                    for (j in col - 1..col + 1) {
                        if ((j != col || i != row) && j in 0..<area.size && i in 0..<area.size) {
                            list.add(area[i][j])
                        }
                    }
                }
                if (list.contains("X")) area[row][col] = list.count { it == "X" }.toString()
            }
            col++
        }
        col = 0
        row++
    }
    return area
}


fun searchFreeCells(
    area: MutableList<MutableList<String>>,
    newArea: MutableList<MutableList<String>>,
    firstArea: MutableList<MutableList<String>>,
    x: Int,
    y: Int
) {
    if (x in 0..<area.size && y in 0..<area.size && area[x][y] != "." && area[x][y] != "*") {
        val nearElement = listOfNotNull(
            area.getOrNull(x - 1)?.getOrNull(y - 1),
            area.getOrNull(x - 1)?.getOrNull(y),
            area.getOrNull(x - 1)?.getOrNull(y + 1),
            area.getOrNull(x)?.getOrNull(y - 1),
            area.getOrNull(x)?.getOrNull(y + 1),
            area.getOrNull(x + 1)?.getOrNull(y - 1),
            area.getOrNull(x + 1)?.getOrNull(y),
            area.getOrNull(x + 1)?.getOrNull(y + 1)
        ).count { it == "/" } > 0
        if (nearElement && area[x][y] != "/") {
            newArea[x][y] = firstArea[x][y]
            area[x][y] = firstArea[x][y]
        }
    }

    if (x < 0 || y < 0 || x >= area.size || y >= area[0].size || (area[x][y] != "." && area[x][y] != "*")) {
        return
    }
    if (area[x][y] == "*") {
        area[x][y] = firstArea[x][y]
        newArea[x][y] = firstArea[x][y]
    } else {
        area[x][y] = "/"
        newArea[x][y] = "/"
    }
    searchFreeCells(area, newArea, firstArea, x + 1, y)
    searchFreeCells(area, newArea, firstArea, x - 1, y)
    searchFreeCells(area, newArea, firstArea, x, y + 1)
    searchFreeCells(area, newArea, firstArea, x, y - 1)
    searchFreeCells(area, newArea, firstArea, x - 1, y - 1)
    searchFreeCells(area, newArea, firstArea, x - 1, y + 1)
    searchFreeCells(area, newArea, firstArea, x + 1, y - 1)
    searchFreeCells(area, newArea, firstArea, x + 1, y + 1)
}

fun setLogic(
    area: MutableList<MutableList<String>>,
    areaWithMine: MutableList<MutableList<String>>,
    colFirst: Int,
    rowFirst: Int
) {
    giveHelp(area)
    var firstRound = true
    var winner = true
    val battleArea = area.map { it.toMutableList() }.toMutableList()
    val areaForVision = MutableList(area.size) { MutableList(area.size) { "." } }
    for (i in areaWithMine.indices) {
        for (j in 0..<area.size) {
            if (areaWithMine[i][j] == "*") {
                battleArea[i][j] = areaWithMine[i][j]
                areaForVision[i][j] = areaWithMine[i][j]
            }
        }
    }
    while ((battleArea.sumOf { it.filter { it == "X" }.size } != 0 &&
                battleArea.sumOf { it.filter { it == "*" }.size } != area.sumOf { it.filter { it == "X" }.size })
        && (battleArea.sumOf { it.filter { it == "." }.size } != 0 ||
                (areaForVision.sumOf { it.filter { it == "." }.size } != battleArea.sumOf { it.filter { it == "X" }.size } + battleArea.sumOf { it.filter { it == "*" }.size }))
    ) {
        while (firstRound) {
            if (area[rowFirst - 1][colFirst - 1] == "X") {
                for (i in area.indices) {
                    for (j in 0..<area.size) {
                        if (area[i][j] == "X") areaForVision[i][j] = area[i][j]
                    }
                }
                println(makePresentableArea(areaForVision))
                println("You stepped on a mine and failed!")
                winner = false
                break
            } else if (area[rowFirst - 1][colFirst - 1] != ".") {
                battleArea[rowFirst - 1][colFirst - 1] = area[rowFirst - 1][colFirst - 1]
                areaForVision[rowFirst - 1][colFirst - 1] = area[rowFirst - 1][colFirst - 1]
            } else {
                searchFreeCells(battleArea, areaForVision, area, rowFirst - 1, colFirst - 1)
            }
            firstRound = false
            println(makePresentableArea(areaForVision))
        }
        println("Set/unset mines marks or claim a cell as free:")
        val (col, row, mod) = readln().split(' ').toList()
        if (mod == "free") {
            if (area[row.toInt() - 1][col.toInt() - 1] == "X") {
                for (i in area.indices) {
                    for (j in 0..<area.size) {
                        if (area[i][j] == "X") areaForVision[i][j] = area[i][j]
                    }
                }
                println(makePresentableArea(areaForVision))
                println("You stepped on a mine and failed!")
                winner = false
                break
            } else if (area[row.toInt() - 1][col.toInt() - 1] != ".") {
                battleArea[row.toInt() - 1][col.toInt() - 1] = area[row.toInt() - 1][col.toInt() - 1]
                areaForVision[row.toInt() - 1][col.toInt() - 1] = area[row.toInt() - 1][col.toInt() - 1]
            } else {
                searchFreeCells(battleArea, areaForVision, area, row.toInt() - 1, col.toInt() - 1)
            }
        } else {
            if (battleArea[row.toInt() - 1][col.toInt() - 1] == "*") {
                battleArea[row.toInt() - 1][col.toInt() - 1] = area[row.toInt() - 1][col.toInt() - 1]
                areaForVision[row.toInt() - 1][col.toInt() - 1] = "."
            } else {
                battleArea[row.toInt() - 1][col.toInt() - 1] = "*"
                areaForVision[row.toInt() - 1][col.toInt() - 1] = "*"
            }
        }
        println(makePresentableArea(areaForVision))
    }
    if (winner) println("Congratulations! You found all the mines!")
}

fun main() {
    println("How many mines do you want on the field?")
    makeArea(9, readln().toInt())
}