import java.util.*

// replication and strength settings
val replicationRate = 100
val virusStrength = 0.5
val complicationsStrength = 10.0
val mutationRate = 0.000006
val baseStrainLength = 10//50

// encoder/decoder settings
val numberOfFeatures = 6
val numberOfMonomers = 4
val bins = mutableListOf<MutableList<Int>>()

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

fun encodeSequence(sequence: String): List<Int> {
    /** encode a sequence to its feature vector
     * 1. loop through each bin of indices
     * 2. for each bin, take the product of the monomers at each index
     * 3. return a list of the products
     * **/

    val transcribedSequence = transcribeToInt(sequence)

    System.out.println(transcribedSequence)

    val features = mutableListOf<Int>()

    bins.forEach { bin ->
        var product = 1
        bin.forEach { index ->
            if (index <= transcribedSequence.size - 1) {
                product = product * transcribedSequence.get(index)
            }
        }
        features.add(product)
    }

    return features.toList()
}

fun decodeSequence2(features: List<Int>): String {
    //System.out.println("2!")
    val decodedSeqMap = HashMap<Int, Int>()

    bins.forEachIndexed { i, bin ->
        val factors = getPrimeFactorization(features[i]).filter { it != 1 }
        bin.forEachIndexed { j, index ->
            decodedSeqMap.set(index, factors[j])
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

fun decodeSequence(features: List<Int>): String {
    /** decode a feature vector to its sequence
     *  1. factor each feature to get bin values
     *  2. order the bin values
     * **/

    val decodedSequenceAsMap = HashMap<Int, Int>() // index : value

    features.forEachIndexed { index, feature ->
        val factors = getFactors(feature)
        val selectedBin = bins[index] // the bin contains indices

        // for each sequence index in the index bin, set the factors equal
        selectedBin.forEachIndexed { indexInBin, indexInSequence ->
            val selectedFactor = factors[indexInBin]
            decodedSequenceAsMap.set(indexInSequence, selectedFactor)
        }
    }

    // turn the map into a string
    val decodedSequenceAsInts = decodedSequenceAsMap.entries
            .sortedBy { entry -> entry.key }
            .map { entry -> entry.value }
            .toMutableList()
    val decodedSequenceAsString = transcribeToString(decodedSequenceAsInts)

    return decodedSequenceAsString
}

/*
val layerRange = 1..numberOfLayers
val secondPart = layerRange.toList().map {
    Math.pow(layerGrowthRate, oddNumbers.take(1).toList()[0].toDouble())
}.sum()
val numberOfWeights = Math.pow(encodingLength.toDouble(), 2.0) /*
fun encodeStrain(strain: String): MutableList<Double> {
    // Encode a strain to its feature vector
    return mutableListOf<Double>()
}

fun decodeVector(vector: MutableList<Double>): String {
    // Decode a feature vector to its strain
    return ""
}* layerRange.toList().map {
    Math.pow(layerGrowthRate, oddNumbers.take(1).toList()[0].toDouble())
}).sum*/
*/

// transcription maps
val monomers = listOf("A", "C", "T", "G")
val transcriptToIntMap = HashMap<String, Int>()
val transcriptToStringMap = HashMap<Int, String>()

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
        if (integer in transcriptToIntMap.values) {
            output.add(integer)
        }
    }
    return output
}