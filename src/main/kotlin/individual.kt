import java.util.*

class Individual(){
    var id = -1

    // health settings
    val maxHealth = 100.0;
    val minHealingRate = 5.0;
    val maxHealingRate = 15.0;
    val healingRate = minHealingRate + (maxHealingRate - minHealingRate) * rand.nextDouble()
    val vulnerability = universalVulnerability

    // a list of this individual's neighbors
    val neighbors = mutableListOf<Individual>()

    // status attributes
    var health = maxHealth
    var immuneSystemStrength = (rand.nextInt(250).toDouble() / 100.0 + 70.0) / 100.0
    var immunity = mutableListOf<Double>()//(1..numberOfFeatures).toMutableList()
    val antibodies = HashMap<String, Int>()
    var complications = 0
    var dead = false

    // the virus populations
    var contractedStrains = HashMap<String, Int>()

    fun init() {
        for (i in (1..numberOfFeatures)) {
            immunity.add(0.0)
        }
    }

    override fun toString() = "$id"

    fun setID(to: Int) {
        id = to
    }

    fun connect(to: Individual) {
        neighbors.add(to)
    }

    fun getStatus() {
        // output status and name
        System.out.print("Hello, my name is " + id)
        if (dead) {
            System.out.println(" (dead)")
        } else {
            System.out.println("")
        }
        System.out.println("Health:\t\t\t\t" + health)
        System.out.println("Healing Rate:\t\t\t" + healingRate)
        System.out.println("Immune System Strength:\t\t" + immuneSystemStrength)
        System.out.println("Total viruses (${contractedStrains.keys.size} strains):\t" + getTotalVirusCount())
        System.out.println("Complications:\t\t\t" + complications)
        System.out.println("")
    }

    fun update() {
        // run full update cycle
        heal()
        increaseVirusPopulations()
        useImmuneSystem()
        transmit()
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
            val encoding = encodeSequence(strain)
            val similarity = determineSimilarity(encoding, immunity.toList())
            var destroyed = 0
            val destroyProbability = similarity //* immuneSystemStrength

            // randomly destroy some of the viruses
            for (i in 1..count) {
                if (rand.nextDouble() <= destroyProbability) {
                    destroyed += 1
                }
            }

            // update contracted strains
            contractedStrains.put(strain, count - destroyed)

            // update immunity
            /*
            val featureToChange = rand.nextInt(numberOfFeatures)
            immunity.zip(encoding).forEachIndexed(({ index, (feature, foreignFeature) ->
                val error = feature - foreignFeature
                immunity.set(index, (immunity[index] + error * 0.0).toInt())
            }))*/
        }
    }

    fun increaseVirusPopulations() {
        /** Increase the virus populations in this Individual **/

        val newViruses = HashMap(contractedStrains)
        // loop through the current viruses
        for ((strain, count) in contractedStrains.entries) {
            //System.out.println(strain)
            // possibly mutate each virus considering similarity to vulnerability and immunity
            val similarityToVulnerability = determineSimilarity(encodeSequence(strain), encodeSequence(vulnerability))
            // if the similarity to vulnerability is greater than threshold, replicate
            if (similarityToVulnerability > bindThreshold) {
                for (i in 0..replicationRate * count) {
                    val virus = mutate(strain)
                    newViruses.set(virus, (newViruses.get(virus) ?: 0) + 1)
                }
            }
        }

        contractedStrains.putAll(newViruses)
    }

    fun transmit() {
        // randomly select up to 500 viruses to transmit to each neighbor
        for (neighbor in neighbors) {
            val transmissionCount = rand.nextInt(500)
            for (i in 0..transmissionCount) {
                if (contractedStrains.keys.size > 0) {
                    val transmittedStrain = contractedStrains.keys.shuffled().elementAt(0)
                    contractedStrains.set(transmittedStrain, contractedStrains.get(transmittedStrain) ?: 1 - 1)
                    neighbor.contractedStrains.set(transmittedStrain, (neighbor.contractedStrains.get(transmittedStrain)
                            ?:0) + 1)
                }
            }
        }
    }

    fun handleComplications() {
        // determine if complications are gotten
        val newComplicationProb = (1.0 - health / maxHealth) * 0.33
        if (rand.nextDouble() <= newComplicationProb) {
            complications += 1
        }
    }

    fun decreaseHealth() {
        // decrease health based on virus count
        val hmViruses = getTotalVirusCount()
        health -= hmViruses * virusStrength
        health -= complications * complicationsStrength
    }

    fun handleDeath() {
        // determine if dead
        dead = health <= 0
        if (dead) {
            System.out.println(id.toString() + " has died!")
        }
    }
}