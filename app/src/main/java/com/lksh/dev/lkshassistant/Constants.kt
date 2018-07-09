package com.lksh.dev.lkshassistant

data class House(val coord: Pair<Double, Double>,
                    val strNumber: String)

object Constant {
    val POINTS = arrayOf(House(Pair(0.34, 0.053), "0"),
            House(Pair(0.43, 0.266), "1"),
            House(Pair(0.41, 0.202), "2"),
            House(Pair(0.425, 0.153), "3"),
            House(Pair(0.483, 0.283), "4"),
            House(Pair(0.321, 0.696), "5"),
            House(Pair(0.311, 0.639), "6"),
            House(Pair(0.473, 0.193), "8"),
            House(Pair(0.523, 0.363), "10"),
            House(Pair(0.571, 0.65), "17"),
            House(Pair(0.556, 0.864), "32"),
            House(Pair(0.579, 0.899), "33"),
            House(Pair(0.588, 0.944), "34"),
            House(Pair(0.48, 0.948), "35")



    )
}