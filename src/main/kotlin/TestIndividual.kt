object TestIndividual {
    @JvmStatic fun main(args: Array<String>) {
        initializeIndexBins()
        initializeTranscriptionMaps()

        val individual = Individual()
        individual.setID(0)

        val strain = universalVulnerability
        //val strain = newStrain()
        individual.contractedStrains.set(strain, 10)

        var i = 0
        var lastHealth = 101.0
        //while (individual.getTotalVirusCount() > 0 && !(individual.health < 0.0)) {
        for (j in (0..7)) {
            lastHealth = individual.health
            System.out.println(i)
            individual.getStatus()
            individual.update()

            System.out.println(lastHealth - individual.health)
            ++i
        }
    }
}