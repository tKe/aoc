package year2022

import InputScope
import PuzzleDefinition
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.google.common.jimfs.PathType
import solveAll
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.PathWalkOption
import kotlin.io.path.fileSize
import kotlin.io.path.fileStore
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.walk

fun main() = repeat(5) {
    solveAll<Day07DSL>(runIterations = 1000)
    solveAll<Day07JimFS>(runIterations = 1)
}

sealed class Day07DSL(body: PuzzleDefinition<Int, Int>, variant: String? = null) :
    Puz22DSL<Int, Int>(7, variant, body)

object Day07 : Day07DSL({
    fun InputScope.dirSizes() = buildList {
        val acc = ArrayDeque<Int>()
        fun cdUp() = acc.removeFirst().also { acc[0] += it }.also(::add)

        for (line in lines) {
            when {
                line == "$ ls" || line.startsWith("dir ") -> {}
                line == "$ cd .." -> cdUp()
                line.startsWith("$ cd ") -> acc.addFirst(0)
                else -> acc[0] += line.substringBefore(' ').toInt()
            }
        }
        repeat(acc.size - 1) { cdUp() } // return to root
        add(acc[0]) // add root
    }

    part1 {
        dirSizes().sumOf { if (it <= 100_000) it else 0 }
    }

    part2 {
        val dirs = dirSizes()
        val free = 70000000 - dirs.last()
        dirs.filter { free + it > 30000000 }.min()
    }
})

object Day07FastFor : Day07DSL({
    fun InputScope.dirSizes() = buildList {
        val acc = ArrayDeque<Int>()
        fun cdUp() = acc.removeFirst().also { acc[0] += it }

        for (line in lines) {
            when {
                line == "$ cd .." -> add(cdUp())
                line == "$ ls" || line[0] == 'd' -> {}
                line[0] == '$' -> acc.addFirst(0)
                else -> acc[0] += line.substringBefore(' ').toInt()
            }
        }
        repeat(acc.size - 1) { add(cdUp()) }
        add(acc[0])
    }

    part1 {
        dirSizes().sumOf { if (it <= 100_000) it else 0 }
    }

    part2 {
        val dirs = dirSizes().toList()
        val free = 70000000 - dirs.last()
        dirs.filter { free + it > 30000000 }.min()
    }
})

object Day07IntArray : Day07DSL({
    fun InputScope.dirSizes() = buildList {
        val acc = IntArray(256)
        var idx = -1
        fun cdUp() = acc[idx--].also(::add).also { acc[idx] += it }

        for (line in lines) {
            when {
                line == "$ cd .." -> cdUp()
                line == "$ ls" || line[0] == 'd' -> {}
                line[0] == '$' -> acc[++idx] = 0
                else -> acc[idx] += line.substringBefore(' ').toInt()
            }
        }

        repeat(idx) { cdUp() } // `cd ..` back to root
        add(acc[0]) // add root size
    }

    part1 {
        dirSizes().sumOf { if (it <= 100_000) it else 0 }
    }

    part2 {
        val dirs = dirSizes()
        val free = 70000000 - dirs.last()
        dirs.filter { free + it > 30000000 }.min()
    }
})

@OptIn(ExperimentalPathApi::class)
object Day07JimFS : Puz22DSL<Int, Int>(7, def = {
    fun InputScope.createFilesystem() = Jimfs.newFileSystem(
        Configuration.builder(PathType.unix())
            .setRoots("/")
            .setWorkingDirectory("/")
            .setAttributeViews("basic")
            .setBlockSize(1)
            .setMaxSize(70_000_000)
            .build()
    ).also { fs ->
        lateinit var cwd: Path
        for (line in lines) {
            when {
                line == "$ cd /" -> cwd = fs.rootDirectories.single()
                line.startsWith("$ ls") -> {} // not used for creating
                line.startsWith("$ cd ") ->
                    cwd = cwd.resolve(line.removePrefix("$ cd ")).normalize()

                line.startsWith("dir ") ->
                    Files.createDirectory(cwd.resolve(line.removePrefix("dir ")))

                else -> {
                    val (sizeStr, fileName) = line.split(' ', limit = 2)
                    Files.write(
                        cwd.resolve(fileName),
                        ByteArray(sizeStr.toInt())
                    )
                }
            }
        }
    }.rootDirectories.single()

    fun Path.directorySize() = walk()
        .sumOf { if (it.isRegularFile()) it.fileSize() else 0 }

    fun Path.directorySizes() = walk(PathWalkOption.INCLUDE_DIRECTORIES)
        .filter { it.isDirectory() }
        .map { it.directorySize() }


    part1 {
        val root = createFilesystem()
        root.directorySizes().sumOf { if (it <= 100_000) it else 0 }.toInt()
    }

    part2 {
        val root = createFilesystem()
        val dirs = root.directorySizes()
        val free = root.fileStore().usableSpace
        dirs.filter { free + it > 30_000_000 }.min().toInt()
    }
})