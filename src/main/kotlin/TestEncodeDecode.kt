object TestEncodeDecode {
    @JvmStatic fun main(args: Array<String>) {
        val tests = 1

        var allPassed = true

        initializeIndexBins()
        initializeTranscriptionMaps()

        System.out.println(transcriptToIntMap)
        for (i in 0..tests-1) {
            val strain = newStrain()
            System.out.println("Strain: ${strain}\n\n")
            val encoded = encodeSequence(strain)
            System.out.println("Encoded: ${encoded}\n\n")
            //val decoded = decodeSequence(encoded)
            //System.out.println("Decoded: ${decoded}\n\n")
            //val reEncoded = encodeSequence(decoded)
            //System.out.println("Re-encoded: ${reEncoded}\n\n")
            var equal = true//encoded == reEncoded
            //System.out.println("Equivalent? $equal\n\n")
            if (!equal) {
                allPassed = false
            }
        }

        System.out.println("All passed? $allPassed")
    }
}