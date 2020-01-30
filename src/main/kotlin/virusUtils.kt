import java.util.*

val replicationRate = 100
val virusStrength = 0.01
val complicationsStrength = 10.0
val mutationRate = 0.000006

fun mutate(strain: String): String {
    // TODO
    return strain
}

fun determineSimilarity(strainA: String, strainB: String): Double {
    // find the percent difference between StrainA and StrainB

    // add whitespace if the strains have different sizes
    var fixedStrainA: String = strainA
    var fixedStrainB: String = strainB
    if (strainA.length > strainB.length) {
        fixedStrainB += " ".repeat(strainA.length - strainB.length)
    } else if (strainA.length < strainB.length) {
        fixedStrainA += " ".repeat(strainB.length - strainA.length)
    }

    val zipped = fixedStrainA.zip(fixedStrainB)
    var matches = 0
    for ((charA, charB) in zipped) {
        if (charA == charB) {
            matches += 1
        }
    }
    return matches.toDouble() / fixedStrainA.length.toDouble()
}