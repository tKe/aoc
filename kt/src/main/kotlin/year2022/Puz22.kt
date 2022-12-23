package year2022

import queryPuzzles
import solveAll

fun queryDay(day: Int? = null) = queryPuzzles { year == 2022 && day == null || day == this.day }
fun solveAll(day: Int? = null) = queryDay(day).solveAll()

fun main() = solveAll { year == 2022 }
