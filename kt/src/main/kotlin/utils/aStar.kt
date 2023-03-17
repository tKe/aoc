package utils
inline fun <T> aStar(start: T, h: T.() -> Double, isEnd: T.() -> Boolean, moves: T.() -> Sequence<Pair<T, Double>>): List<T> {
    val open = mutableSetOf(start)
    val gScore = mutableMapOf(start to 0.0)
    val fScore = mutableMapOf(start to start.h())
    val cameFrom = mutableMapOf<T, T>()
    while(open.isNotEmpty()) {
        val current = open.minBy(fScore::getValue)
        if(current.isEnd()) return generateSequence(current, cameFrom::get).toList().asReversed()
        open -= current
        current.moves().forEach { (neighbour, distance) ->
            val score = gScore.getValue(current) + distance
            if(score < gScore.getOrDefault(neighbour, Double.POSITIVE_INFINITY)) {
                cameFrom[neighbour] = current
                gScore[neighbour] = score
                fScore[neighbour] = score + neighbour.h()
                open += neighbour
            }
        }
    }
    return emptyList()
}
