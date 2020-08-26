import kotlin.math.*

object TestIndividual {
    @JvmStatic fun main(args: Array<String>) {
        initializeIndexBins()
        initializeTranscriptionMaps()

        val focus = 79 // ID of individual to follow

        System.out.println("Creating virus...")
        val strainAsMutableList = newStrain().slice(0..bindSiteLength).split("").toMutableList()
        strainAsMutableList.addAll(0, universalVulnerability.split(""))
        val strain = strainAsMutableList.joinToString("")
        //val strain = newStrain()

        System.out.println("Running tests...")
        var dead = 0
        val trials = 100
        val trialLength = 365 * 10
        for (trial in (1..trials)) {
            val individual = Individual()
            individual.setID(trial)

            individual.contractedStrains.set(strain, 1)

            var i = 0
            var lastHealth = 101.0
            //while (individual.getTotalVirusCount() > 0 && !(individual.health < 0.0)) {
            while (!individual.dead && individual.getTotalVirusCount() > 0 && i <= trialLength - 1) {
                if (individual.id == focus) {
                    System.out.println("Day $i")
                    individual.getStatus()
                }
                lastHealth = individual.health
                individual.update()
                ++i
            }

            System.out.println("Day $i")
            individual.getStatus()

            if (lastHealth > individual.health || individual.dead) {//(individual.dead) {
                dead += 1
            }

            System.gc()
        }

        System.out.println("\nFailures: $dead / $trials")
    }
}