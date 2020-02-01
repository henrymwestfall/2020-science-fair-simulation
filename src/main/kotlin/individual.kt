import java.util.*

class Individual(){
    var id = -1

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

    override fun toString() = "$id"

    fun setID(to: Int) {
        id = to
    }

    fun connect(to: Individual) {
        neighbors.add(to)
    }

    fun getStatus() {
        // output status and name
        System.out.println("Hello, my name is " + id)
        System.out.println("Health:\t\t\t" + health)
        System.out.println("Healing Rate:\t\t" + healingRate)
        System.out.println("Immune System Strength:\t" + immuneSystemStrength)
        System.out.println("")
    }

    fun update() {
        // run full update cycle
        heal()
        useImmuneSystem()
        increaseVirusPopulations()
        handleComplications()
        decreaseHealth()
        handleDeath()
    }

    fun getTotalVirusCount(): Int {
        // count the total number of viruses in this individual
        var totalCount = 0
        for (count in contractedStrains.values) {
            totalCount += count
        }

        return totalCount
    }

    private fun heal() {
        // Increase the health and cap it at the maximum health
        health += healingRate
        if (health > maxHealth) {
            health = maxHealth
        }
    }

    private fun useImmuneSystem() {
        // loop through contracted strains
        for ((strain, count) in contractedStrains) {
            // determine the destroy probability
            val similarity: Double = determineSimilarity(strain, immunity)
            var destroyed = 0
            val destroyProbability = similarity * immuneSystemStrength

            // randomly destroy some of the viruses
            for (i in 1..count) {
                if (rand.nextDouble() <= destroyProbability) {
                    destroyed += 1
                }
            }

            // update contracted strains
            contractedStrains.put(strain, count - destroyed)
        }
    }

    private fun increaseVirusPopulations() {
        // Increase the virus populations in this Individual

        val newViruses = HashMap<String, Int>()
        // loop through the current viruses
        for (strain in contractedStrains.keys) {
            // possible mutate each virus
            for (i in 0..replicationRate) {
                val virus = mutate(strain)
                if (virus in newViruses) {
                    newViruses.put(virus, newViruses.get(virus)?:0 + 1)
                } else {
                    newViruses.put(virus, 1)
                }
            }
        }

        contractedStrains = newViruses
    }

    private fun handleComplications() {
        // determine if complications are gotten
        val newComplicationProb = 1.0 - health / maxHealth
        if (rand.nextDouble() <= newComplicationProb) {
            complications += 1
        }
    }

    private fun decreaseHealth() {
        // decrease health based on virus count
        val hmViruses = getTotalVirusCount()
        health -= hmViruses * virusStrength
        health -= complications * complicationsStrength
    }

    private fun handleDeath() {
        // determine if dead
        dead = health <= 0
    }
}