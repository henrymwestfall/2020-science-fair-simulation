import java.util.*
import kotlin.math.*

class Individual(){
    var id = -1

    // health settings
    val maxHealth = 100.0;
    val minHealingRate = 1.0;
    val maxHealingRate = 5.0;
    val healingRate = minHealingRate + (maxHealingRate - minHealingRate) * rand.nextDouble()
    val vulnerability = universalVulnerability

    // a list of this individual's neighbors
    val neighbors = mutableListOf<Individual>()

    // status attributes
    var health = maxHealth
    val maxImmuneSystemStrength = (rand.nextInt(60) + 30).toDouble()
    val lowImmuneSystemStrength = (rand.nextInt(30) + 20).toDouble()
    var immuneSystemStrength = 0.0

    var immunity = mutableListOf<Double>()//(1..numberOfFeatures).toMutableList()
    val antibodies = HashMap<String, Int>()
    var complications = 0
    var dead = false
    val essentiallyDeadThreshold = 0.5
    var age = 35 * 365 // age in days (35 years old)

    // the virus populations
    var contractedStrains = HashMap<String, Long>()

    init {
        for (i in (1..numberOfFeatures)) {
            immunity.add(-0.5)
        }
        updateImmuneSystemStrength()
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
        System.out.println("Age (years):\t\t\t${age.toDouble()/365.0}")
        System.out.println("Health:\t\t\t\t" + health)
        System.out.println("Healing Rate:\t\t\t" + healingRate)
        System.out.println("Immune System Strength:\t\t" + immuneSystemStrength)
        System.out.println("Total viruses (${contractedStrains.keys.size} strains):\t" + getTotalVirusCount())
        System.out.println("Complications:\t\t\t" + complications)
        System.out.println("")
    }

    fun updateImmuneSystemStrength() {
        val a = -(lowImmuneSystemStrength - maxImmuneSystemStrength).toDouble() / 900.0
        fun f(x: Double) = -a * (x - 35).toDouble().pow(2.0) + maxImmuneSystemStrength
        fun g(x: Double) = f(1.0 / 365.0 * x) / 100.0
        immuneSystemStrength = g(age.toDouble())
    }

    fun update() {
        // run full update cycle
        ++age
        if (!dead) {
            heal()
            increaseVirusPopulations()
            useImmuneSystem()
            handleComplications()
            decreaseHealth()
            handleDeath()
            if (!dead) {
                transmit()
            }
        }
    }

    fun getTotalVirusCount(): Long {
        // count the total number of viruses in this individual
        var totalCount = 0L
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
        val removals = mutableListOf<String>()
        for ((strain, count) in contractedStrains) {



            // determine the destroy probability
            val encoding = encodeSequence(strain)

            // update immunity
            (immunity zip encoding).forEachIndexed(({ index, (feature, foreignFeature) ->
                val error: Double = foreignFeature - feature
                //System.out.println("$index: $error")
                immunity.set(index, immunity[index] + immuneSystemStrength * error * 0.1)
            }))

            val immunityList = immunity.toList()
            val similarity = determineSimilarity(encoding, immunityList)
            //System.out.println(encoding)

            /*val maxKills = if (count < 100000) count else 100000
            val destroyed = (similarity * maxKills).roundToInt()

            System.out.println(destroyed)

            if (destroyed == 0) {
                //System.out.println(similarity)
            }

            // update contracted strains
            contractedStrains.put(strain, count - destroyed)
            */
            //System.out.println(similarity)
            if (similarity > 0.9) {
                val remaining = 0L//if (count - 10000000 > 0) 0 else count - 10000000
                contractedStrains.put(strain, remaining)
                if (contractedStrains.get(strain) ?: 0 <= 0) {
                    removals.add(strain)
                }
            }
            //System.out.println(determineSimilarity(immunity, encoding))
        }

        removals.forEach({strain ->
            contractedStrains.remove(strain)
        })
    }

    fun increaseVirusPopulations() {
        /** Increase the virus populations in this Individual **/

        if (health > 0.5) {
            val newViruses = HashMap(contractedStrains)
            // loop through the current viruses
            for ((strain, count) in contractedStrains.entries) {
                //System.out.println(strain)
                // possibly mutate each virus considering similarity to vulnerability and immunity
                //val similarityToVulnerability = determineSimilarity(encodeSequence(strain), encodeSequence(vulnerability))
                // if the similarity to vulnerability is greater than threshold, replicate
                if (vulnerability in strain) {
                    val numberOfSpawns = sqrtReplicationRate * count
                    val mutations = (numberOfSpawns.toDouble() * mutationRate).roundToInt()
                    for (i in 1..mutations){//replicationRate * count) {
                        val virus = mutate(strain)
                        if (virus != strain) {
                            //System.out.println("MUTATION")
                        }
                        newViruses.set(virus, (newViruses.get(virus) ?: 0) + sqrtReplicationRate)
                    }
                }
            }

            contractedStrains.putAll(newViruses)
        }
    }

    fun transmit() {
        // maybe randomly select viruses to transmit to each neighbor
        if (rand.nextDouble() <= 0.5 && health >= essentiallyDeadThreshold * maxHealth && complications == 0) {

            for (neighbor in neighbors) {
                val transmissionCount = rand.nextInt(20)
                for (i in 1..transmissionCount) {
                    if (contractedStrains.keys.size > 0) {
                        val transmittedStrain = contractedStrains.keys.shuffled().elementAt(0)
                        contractedStrains.set(transmittedStrain, contractedStrains.get(transmittedStrain) ?: 1 - 1)
                        neighbor.contractedStrains.set(
                            transmittedStrain, (neighbor.contractedStrains.get(transmittedStrain)
                                ?: 0) + 1
                        )
                    }
                }
            }
        }
    }

    fun handleComplications() {
        // determine if complications are gotten
        val newComplicationProb = getTotalVirusCount() / complicationDeveloplmentDenominator
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
        var ofAge = false
        if (age >= 80 * 365) {
            dead = true
            ofAge = true
        }
        dead = health <= 0
        /*if (dead) {
            System.out.print(id.toString() + " has died")
            if (ofAge) {
                System.out.println("of old age!")
            }
            else {
                System.out.println("!")
            }
        }*/
    }
}