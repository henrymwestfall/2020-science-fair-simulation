import java.util.*

val rand = Random(1)

class Individual(var name: Int) {
    // health settings
    val maxHealth = 100.0;
    val minHealingRate = 5.0;
    val maxHealingRate = 15.0;
    val healingRate = minHealingRate + (maxHealingRate - minHealingRate) * rand.nextDouble()

    // a list of this individual's neighbors
    val neighbors = mutableListOf<Individual>()

    // status attributes
    var health = maxHealth
    var immuneSystemStrength = rand.nextDouble()
    var immunity = ""
    var complications = 0
    var dead = false

    // the virus populations
    var contractedStrains = HashMap<String, Int>()

    fun getStatus() {
        // output status and name
        System.out.println("Hello, my name is " + name)
        System.out.println("Health:\t\t" + health)
        System.out.println("Healing Rate:\t\t" + healingRate)
        System.out.println("Immune System Strenght:\t")
        System.out.println("")
    }

    fun heal() {
        // Increase the health and cap it at the maximum health
        health += healingRate
        if (health > maxHealth) {
            health = maxHealth
        }
    }

    fun useImmuneSystem() {
        // loop through contracted strains
        for ((strain, count) in contractedStrains) {
            // determine the destroy probability
            val similarity = determineSimilarity(strain, immunity)
            var destroyed = 0
            val destroyProbability = similarity * immuneSystemStrength

            // randomly destroy some of the viruses
            for (i in 1..count) {
                if (rand.nextDouble() <= destroyProbability) {
                    destroyed += 1
                }
            }

            // update contracted strains
            contractedStrains.put(virus, count - destroyed)
        }
    }

    fun increaseVirusPopulations() {
        // Increase the virus populations in this Individual

        val newViruses = HashMap<String, Int>
        // loop through the current viruses
        for ((strain, count) in contractedStrains) {
            // possible mutate each virus
            for (i in 0..replicationRate) {
                val virus = mutate(strain)
                if (virus in newViruses) {
                    newViruses.put(virus, newViruses.get(virues) + 1)
                } else {
                    newViruses.put(virus, 1)
                }
            }
        }

        contractedStrains = newViruses


    }
}