package year2024

import aok.PuzDSL
import aok.checkAll
import aok.solveAll
import aok.warmup
import aoksp.AoKSolution
import kotlin.time.Duration.Companion.seconds

@AoKSolution
object Day09 : PuzDSL({
    part1 {
        val disk =
            input.flatMapIndexed { index, c -> List(c.digitToInt()) { if (index and 1 == 0) index shr 1 else -1 } }
                .toIntArray()

        var nextFree = disk.indexOf(-1)
        var lastFile = disk.indexOfLast { it != -1 }
        while (nextFree < lastFile) {
            disk[nextFree] = disk[lastFile]
            disk[lastFile] = -1
            while (disk[nextFree] != -1) nextFree++
            while (disk[lastFile] == -1) lastFile--
        }
        disk.mapIndexed { index, i -> if (i > 0) index * i.toLong() else 0 }.sum()
    }

    part2 {
        val ids = input.indices.map { index -> if (index and 1 == 0) index shr 1 else -1 }.toMutableList()
        val sizes = input.map { it.digitToInt() }.toMutableList()
        fun MutableList<Int>.swap(a: Int, b: Int) = get(a).let { set(a, get(b)); set(b, it) }

        for (file in ids.max() downTo 0) {
            val fileIdx = ids.indexOf(file)
            val fileSize = sizes[fileIdx]

            val freeIndex = sizes.withIndex().firstOrNull { (sizeIdx, size) ->
                ids[sizeIdx] == -1 && size >= fileSize
            }?.index

            if (freeIndex != null && freeIndex < fileIdx) {
                val free = sizes[freeIndex]
                if (fileSize == free) {
                    ids.swap(fileIdx, freeIndex)
                    sizes.swap(fileIdx, freeIndex)
                } else {
                    ids.swap(fileIdx, freeIndex)
                    sizes[freeIndex] = fileSize
                    ids.add(freeIndex + 1, -1)
                    sizes.add(freeIndex + 1, free - fileSize)
                }

                // merge frees
                for (i in ids.lastIndex downTo 1) {
                    if (ids[i] == -1 && ids[i - 1] == -1) {
                        sizes[i - 1] += sizes[i]
                        ids.removeAt(i)
                        sizes.removeAt(i)
                    }
                }
            }
        }

        var chk = 0L
        var blk = 0
        for ((idx, id) in ids.withIndex()) {
            repeat(sizes[idx]) {
                if (id > 0) chk += blk * id
                blk++
            }
        }
        chk
    }
})

@AoKSolution
object Day09Range : PuzDSL({
    part1 {
        val disk =
            input.flatMapIndexed { index, c -> List(c.digitToInt()) { if (index and 1 == 0) index shr 1 else -1 } }
                .toIntArray()

        var nextFree = disk.indexOf(-1)
        var lastFile = disk.indexOfLast { it != -1 }
        while (nextFree < lastFile) {
            disk[nextFree] = disk[lastFile]
            disk[lastFile] = -1
            while (disk[nextFree] != -1) nextFree++
            while (disk[lastFile] == -1) lastFile--
        }

        disk.mapIndexed { index, i -> if (i > 0) index * i.toLong() else 0 }.sum()
    }

    part2 {
        val disk =
            input.flatMapIndexed { index, c -> List(c.digitToInt()) { if (index and 1 == 0) index shr 1 else -1 } }
                .toIntArray()

        fun IntArray.indexOf(value: Int, startIndex: Int = 0): Int {
            for(at in startIndex..lastIndex) if(get(at) == value) return at
            return -1
        }

        for (fileId in disk.max() downTo 1) {
            val fileStart = disk.indexOfFirst { it == fileId }
            val fileEnd = disk.indexOfLast { it == fileId }
            val fileSize = fileEnd - fileStart

            println(disk.joinToString("") { if (it == -1) "." else "${it.digitToChar(36)}" })

            var free = disk.indexOf(-1)
            var end = free
            while (free < fileStart - fileSize) {
                var size = 1
                while (disk[end++] == -1) size++
                if (size > fileSize) break
                else free = disk.indexOf(-1, end)
            }

            val shift = free - fileStart
            if (shift < fileSize) {
                for (i in fileStart..fileEnd) {
                    disk[i].let { disk[i] = disk[i + shift]; disk[i + shift] = it }
                }
            }
        }

        disk.mapIndexed { index, i -> if (i > 0) index * i.toLong() else 0 }.sum()
    }
})

fun main() = queryDay(9)
    .checkAll(input = "2333133121414131402", part1 = 1928L, part2 = 2858L)
    .checkAll(part1 = 6386640365805L, part2 = 6423258376982L)
    .warmup(10.seconds)
    .solveAll()
