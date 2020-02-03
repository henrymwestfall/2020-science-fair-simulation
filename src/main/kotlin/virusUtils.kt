import java.util.*

// replication and strength settings
val replicationRate = 100
val virusStrength = 0.5
val complicationsStrength = 10.0
val mutationRate = 0.000006
val baseStrainLength = 50

// encoder/decoder settings
val layerGrowthRate = 2.5
val numberOfLayers = 5
val encodingLength = 6 // yields polymers with 586 monomers

val oddNumbers = sequence {
    var i = 1
    while (true) {
        if (i % 2 == 0) { // is even
            ++i
        } else {
            yield(i++)
        }
    }
}

/*
val layerRange = 1..numberOfLayers
val secondPart = layerRange.toList().map {
    Math.pow(layerGrowthRate, oddNumbers.take(1).toList()[0].toDouble())
}.sum()
val numberOfWeights = Math.pow(encodingLength.toDouble(), 2.0) /** layerRange.toList().map {
    Math.pow(layerGrowthRate, oddNumbers.take(1).toList()[0].toDouble())
}).sum*/
*/

// transcription maps
val monomers = listOf("A", "C", "T", "G")
val transcriptToIntMap = HashMap<String, Int>()
val transcriptToStringMap = HashMap<Int, String>()

fun initializeTranscriptionMaps() {
    var index = 0
    for (monomer in monomers) {
        transcriptToIntMap.set(monomer, index)
        transcriptToStringMap.set(index, monomer)
        ++index
    }
}

fun encodeStrain(strain: String): MutableList<Double> {
    // Encode a strain to its feature vector
    return mutableListOf<Double>()
}

fun decodeVector(vector: MutableList<Double>): String {
    // Decode a feature vector to its strain
    return ""
}

fun newStrain(): String {
    // Create a random new polymer
    var strain = ""
    for (i in 0..baseStrainLength) {
        strain += monomers[rand.nextInt(monomers.size)]
    }
    return strain
}

fun mutate(strain: String): String {
    val mutates = rand.nextDouble() <= mutationRate
    if (mutates) {
        val splitStrain = strain.split("")
        val newStrain = mutableListOf<String>()
        for (monomer in splitStrain) {
            var addedMonomer = monomer
            val type = listOf("insertion", "deletion", "change")[rand.nextInt(2)] // choose a random mutation type
            if (type == "insertion") {
                // insert before this monomer
                newStrain.add(monomers[rand.nextInt(monomers.size)])
            } else if (type == "deletion") {
                // delete this monomer
                addedMonomer = ""
            } else if (type == "change") {
                // change this monomer randomly
                addedMonomer = monomers[rand.nextInt(monomers.size)]
            }

            newStrain.add(addedMonomer)
        }
        return newStrain.joinToString()
    }
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

fun transcribeToString(inputIntList: MutableList<Int>): String {
    var output = ""
    for (integer in inputIntList) {
        output += transcriptToStringMap.get(integer)?:""
    }
    return output
}

fun transcribeToInt(inputString: String): MutableList<Int> {
    var output = mutableListOf<Int>()
    for (monomer in inputString.split("")) {
        val integer = transcriptToIntMap.get(monomer)?:0
        output.add(integer)
    }
    return output
}