
import aok.*
import year2018.AoKYear2018
import year2019.AoKYear2019
import year2020.AoKYear2020
import year2022.AoKYear2022
import kotlin.reflect.KClass

// manually register sealed roots here (can't cross package boundaries)
private val years: List<KClass<out Puz<*, *>>> = listOf(
//    AoKYear2015::class,
//    AoKYear2016::class,
//    AoKYear2017::class,
    AoKYear2018::class,
    AoKYear2019::class,
    AoKYear2020::class,
    AoKYear2022::class
)

fun main(): Unit = with(InputProvider) {
    years.flatMap { it.sealedObjects }
        .sortedWith(compareBy(PuzKey::year, PuzKey::day, PuzKey::variant)).solveAll()
}
