import aok.*
import year2015.AoKYear2015
import year2016.AoKYear2016
import year2017.AoKYear2017
import year2022.AoKYear2022
import kotlin.reflect.KClass

// manually register sealed roots here (can't cross package boundaries)
private val years: List<KClass<out Puz<*, *>>> = listOf(
    AoKYear2015::class,
    AoKYear2016::class,
    AoKYear2017::class,
    AoKYear2022::class
)

fun main(): Unit = with(InputProvider) {
    years.flatMap { it.sealedObjects }
        .sortedWith(compareBy(PuzKey::year, PuzKey::day, PuzKey::variant)).solveAll()
}
