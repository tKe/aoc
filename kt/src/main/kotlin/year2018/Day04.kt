package year2018

import aok.PuzDSL
import aoksp.AoKSolution
import kotlinx.datetime.LocalDateTime
import year2018.Day04.maxBy

@AoKSolution
object Day04 : PuzDSL({

    val logParser = parser {
        buildList {
            var currentGuard = Guard(0)
            lines.sorted() // hooray for lexicographically ordered date-times
                .forEach {
                    val (dt, entry) = it.split("] ", limit = 2)
                    val ts = LocalDateTime.parse(dt.trimStart('[').replace(' ', 'T'))
                    add(
                        when {
                            entry.endsWith(" begins shift") -> {
                                currentGuard = Guard(entry.split('#', ' ').mapNotNull(String::toIntOrNull).single())
                                ShiftStart(ts, currentGuard)
                            }

                            entry == "falls asleep" -> SleepStart(ts, currentGuard)
                            entry == "wakes up" -> SleepEnd(ts, currentGuard)
                            else -> TODO(entry)
                        }
                    )
                }
        }
    }

    fun List<Entry>.sleepsByGuard() = groupBy { it.guard }
        .mapValues { (_, entries) ->
            entries.filter { it !is ShiftStart }
                .chunked(2) { (sleep, wake) ->
                    require(sleep is SleepStart && wake is SleepEnd)
                    sleep.timestamp.minute..<wake.timestamp.minute
                }
        }

    part1 {
        val log = logParser()
        val (guard, sleeps) = log.sleepsByGuard().maxBy { (_, sleeps) -> sleeps.sumOf { 1 + it.last - it.first } }
        val mostSleptMinute = sleeps.flatten().groupingBy { it }.eachCount().maxBy { it.value }.key
        guard.id * mostSleptMinute
    }

    data class MostSlept(val guard: Guard, val minute: Int, val count: Int)
    part2 {
        val log = logParser()
        val sleeps = log.sleepsByGuard()

        val mostSlepts = sleeps.flatMap { (guard, s) ->
            s.flatten().groupingBy { it }.eachCount()
                .map { (minute, count) -> MostSlept(guard, minute, count) }
        }
        val guardMostSlept = mostSlepts.groupingBy { it.guard }.maxBy { it.count }.values
        val minMostSlept = mostSlepts.groupingBy { it.minute }.maxBy { it.count }
        guardMostSlept.filter { minMostSlept[it.minute] == it }
            .maxBy { it.count }
            .let { it.guard.id * it.minute }
    }
}) {
    @JvmInline
    value class Guard(val id: Int)
    sealed class Entry(val timestamp: LocalDateTime, val guard: Guard)
    class ShiftStart(timestamp: LocalDateTime, guard: Guard) : Entry(timestamp, guard)
    class SleepStart(timestamp: LocalDateTime, guard: Guard) : Entry(timestamp, guard)
    class SleepEnd(timestamp: LocalDateTime, guard: Guard) : Entry(timestamp, guard)

    inline fun <T, K, R : Comparable<R>> Grouping<T, K>.maxBy(crossinline selector: (T) -> R) =
        aggregate { _, m: T?, e, _ -> m?.takeIf { selector(it) > selector(e) } ?: e }
}

fun main(): Unit = solveDay(
    4,
//    input = InputProvider.raw(
//        """
//        [1518-11-01 00:00] Guard #10 begins shift
//        [1518-11-01 00:05] falls asleep
//        [1518-11-01 00:25] wakes up
//        [1518-11-01 00:30] falls asleep
//        [1518-11-01 00:55] wakes up
//        [1518-11-01 23:58] Guard #99 begins shift
//        [1518-11-02 00:40] falls asleep
//        [1518-11-02 00:50] wakes up
//        [1518-11-03 00:05] Guard #10 begins shift
//        [1518-11-03 00:24] falls asleep
//        [1518-11-03 00:29] wakes up
//        [1518-11-04 00:02] Guard #99 begins shift
//        [1518-11-04 00:36] falls asleep
//        [1518-11-04 00:46] wakes up
//        [1518-11-05 00:03] Guard #99 begins shift
//        [1518-11-05 00:45] falls asleep
//        [1518-11-05 00:55] wakes up
//    """.trimIndent()
//    ),
)
