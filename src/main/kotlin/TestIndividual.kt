import kotlin.math.*

object TestIndividual {
    @JvmStatic fun main(args: Array<String>) {
        initializeIndexBins()
        initializeTranscriptionMaps()

        val focus = 1 // ID of individual to follow

        System.out.println("Creating virus...")
        var strain = universalVulnerability
        for (i in 0..10) {
            strain = mutate(strain, true)
        }
        //val strain = newStrain()

        System.out.println("Running tests...")
        var dead = 0
        for (trial in (1..100)) {
            val individual = Individual()
            individual.setID(trial)

            individual.contractedStrains.set(strain, 10)

            var i = 0
            var lastHealth = 101.0
            //while (individual.getTotalVirusCount() > 0 && !(individual.health < 0.0)) {
            while (!individual.dead && individual.getTotalVirusCount() > 0 && i <= 365) {
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

            if (individual.dead) {
                dead += 1
            }

            System.gc()
        }

        System.out.println("\nFailures per 100: $dead")
    }
}