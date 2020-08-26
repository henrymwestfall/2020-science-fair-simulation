import java.util.*
import kotlin.math.*

// replication and strength settings
val replicationRate = 30
var sqrtReplicationRate = 0//sqrt(replicationRate).roundToInt()
val virusStrength = 0.0
val complicationsStrength = 8.0
val complicationDeveloplmentDenominator = 10000.0
val mutationRate = 0.00001
val baseStrainLength = 500
val maxStrainLength = 550
val bindThreshold = 0.50
val bindSiteLength = (baseStrainLength * 0.05).toInt()

// encoder/decoder settings
val numberOfFeatures = 10
val bins = mutableListOf<MutableList<Int>>() // tells which feature each index goes to

// transcription maps
val monomers = listOf("A", "C", "T", "G")
val transcriptToIntMap = HashMap<String, Int>()
val transcriptToStringMap = HashMap<Int, String>()


fun initialize() {
    initializeTranscriptionMaps()
    initializeIndexBins()
    sqrtReplicationRate = sqrt(replicationRate.toDouble()).roundToInt()
}


fun initializeTranscriptionMaps() {
    val values = getPrimeNumbers(monomers.size)
    monomers.forEachIndexed { i, monomer ->
        transcriptToIntMap.set(monomer, values[i])
        transcriptToStringMap.set(values[i], monomer)
    }
}


fun initializeIndexBins() {
    for (i in 0..numberOfFeatures-1) {
        val bin = mutableListOf<Int>()
        bins.add(bin)
    }

    for (i in 0..baseStrainLength-1) {
        val binIndex = i % numberOfFeatures
        bins[binIndex].add(i)
    }
}


fun newStrain(): String {
    // Create a random new polymer
    var strain = ""
    for (i in 0..baseStrainLength-1) {
        strain += monomers[rand.nextInt(monomers.size)]
    }
    return strain
}

val universalVulnerability = newStrain().slice(0..bindSiteLength)

fun getFactors(num: Int): MutableList<Int> {
    /** return the factors of num **/
    val factors = mutableListOf<Int>()
    if (num < 1) {
        return factors
    }

    (1..(num / 2).toInt())
            .filter { num % it == 0 }
            .forEach { factors.add(it) }
    factors.add(num)

    return factors
}


fun getPrimeFactorization(num: Int, debug: Boolean=false): MutableList<Int> {
    val factorization = mutableListOf<Int>()
    primeFactorizationHelper(num, factorization, 0, debug)
    return factorization
}


fun primeFactorizationHelper(num: Int, factorization: MutableList<Int>, tabs: Int, debug: Boolean) {
    val factors = getFactors(num)
    val tabText = "\t".repeat(tabs)
    if (debug) {
        System.out.print(tabText)
    }
    if (factors.size == 2) {
        if (debug) {
            System.out.println("Found $num")
        }
        factorization.add(num)
    } else {
        if (debug) {
            System.out.println("Expanding $num")
        }
        val availableFactors = factors.filter { it != 1 && it != num }
        val factor = num / availableFactors[0]
        val other = num / factor
        primeFactorizationHelper(factor, factorization, tabs + 1, debug)
        primeFactorizationHelper(other, factorization, tabs + 1, debug)
    }
}


fun getPrimeNumbers(count: Int): MutableList<Int> {
    /** return a list of the first 'count' prime numbers **/
    val primes = mutableListOf<Int>()
    var num = 0
    while (primes.size < count) {
        if (getFactors(num).size == 2) { // 2 factors, one is 1 and the other is num
            primes.add(num)
        }
        ++num
    }
    return primes
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
        if (integer in transcriptToIntMap.values) {
            output.add(integer)
        }
    }
    return output
}


fun encodeSequence(sequence: String): List<Double> {
    /** encode a sequence to its feature vector
     * 1. loop through each bin of indices
     * 2. for each bin, take the product of the monomers at each index
     * 3. return a list of the products
     * **/

    val transcribedSequence = transcribeToInt(sequence)

    //System.out.println(transcribedSequence)

    val features = mutableListOf<Double>()

    bins.forEach { bin ->
        var sum = 0
        bin.forEach { index ->
            if (index <= transcribedSequence.size - 1) {
                sum += transcribedSequence.get(index)
            }
        }
        features.add(sum.toDouble() / bin.size.toDouble())
    }

    return features.toList()
}


fun decodeSequence(features: List<Int>): String {
    //System.out.println("2!")
    val decodedSeqMap = HashMap<Int, Int>()

    bins.forEachIndexed { i, bin ->
        val factors = getPrimeFactorization(features[i]).filter { it != 1 }
        bin.forEachIndexed { j, index ->
            decodedSeqMap.put(index, factors[j])
        }
    }

    // turn the map into a string
    val decodedSequenceAsInts = decodedSeqMap.entries
            .sortedBy { entry -> entry.key }
            .map { entry -> entry.value }
            .toMutableList()
    val decodedSequenceAsString = transcribeToString(decodedSequenceAsInts)

    System.out.println(decodedSeqMap)
    return decodedSequenceAsString
}


fun mutate(strain: String, force: Boolean = false): String {
    val mutates = rand.nextDouble() <= mutationRate

    if (mutates || force) {
        val splitStrain = strain.split("").toMutableList()
        val index = rand.nextInt(strain.length - 1)
        var type = listOf("insertion", "deletion", "change")[rand.nextInt(2)] // choose a random mutation type

        if (type == "insertion" && strain.length == maxStrainLength) {
            type = "deletion"
        }
        if (type == "insertion") {
            // insert at this index
            splitStrain.add(index, monomers[rand.nextInt(monomers.size - 1)])
        } else if (type == "deletion") {
            // delete this monomer
            splitStrain.removeAt(index)
        } else if (type == "change") {
            // change this monomer randomly
            splitStrain.set(index, monomers[rand.nextInt(monomers.size - 1)])
        }

        return splitStrain.joinToString("")
    }

    return strain
}


fun getSimilarityFromRawDifference(x: Double): Double {
    val mc: Double = (monomers.size * numberOfFeatures).toDouble()
    val horizontalStretch: Double = (8.0 * ln(3.0)) / mc
    var similarityOutOf100: Double = 100.0 * (1.0 / (1.0 + exp(horizontalStretch * (x - mc / 2.0))))
    if (similarityOutOf100 >= 98.0) {
        similarityOutOf100 = 100.0
    }
    return similarityOutOf100 / 100.0
}


fun determineSimilarity1(strainAEncoding: List<Double>, strainBEncoding: List<Double>, doPrint: Boolean = false): Double {
    var sumDifference = 0.0
    (strainAEncoding zip strainBEncoding).forEach { (featureA, featureB) ->
        sumDifference += abs(featureA - featureB)
    }
    if (sumDifference != 0.0 && doPrint) {
        System.out.println(sumDifference)
    }
    //return 1.0 - (sumDifference / monomers.size.toDouble())
    return getSimilarityFromRawDifference(sumDifference)

}


fun determineSimilarity(strainAEncoding: List<Double>, strainBEncoding: List<Double>, doPrint: Boolean = false): Double {
    var numberOfDifferences = 0.0
    val length = strainAEncoding.size.toDouble()
    (strainAEncoding zip strainBEncoding).forEach({ (featureA, featureB) ->
        if ((featureA - featureB).absoluteValue > 0.1) {
            numberOfDifferences += 1.0
        }
    })
    assert(length != 0.0 && numberOfDifferences != 0.0)
    val returnValue = 1.0 - numberOfDifferences / length
    return returnValue
}


fun getHammingDistance(strainA: String, strainB: String): Double {
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