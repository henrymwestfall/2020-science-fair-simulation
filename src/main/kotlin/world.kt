import java.util.*

class World(val connectivity: Double) {
    var nextID = 0
    val communities = mutableListOf<Community>()

    fun addCommunity(newCommunity: Community) {
        // maybe connect to other communities
        newCommunity.setID(nextID++)
        for (community in communities) {
            if (rand.nextDouble() <= connectivity) {
                community.connect(newCommunity)
                newCommunity.connect(community)
            }
        }

        communities.add(newCommunity)
    }

    fun generateCommunities(count: Int, minConnectivity: Double, maxConnectivity: Double, minPopulation: Int, maxPopulation: Int) {
        for (i in 0..count) {
            val connectivity = minConnectivity + (maxConnectivity - minConnectivity) * rand.nextDouble()
            val population = (minPopulation..maxPopulation).random()
            val community = Community(connectivity, population)
            community.generateIndividuals()
            addCommunity(community)
        }
    }

    fun update() {
        for (community in communities) {
            community.update()
        }
    }

    fun getVirusCount(): Int {
        var count = 0
        for (community in communities) {
            count += community.getVirusCount()
        }
        return count
    }

    fun introducePathogens(count: Int) {
        // Infect some individuals randomly
        for (i in 0..count) {
            val strain = newStrain()
            val targetCommunity = communities[rand.nextInt(communities.size - 1)]
            val targetIndividual = targetCommunity.individuals[rand.nextInt(communities.size - 1)]
            targetIndividual.contractedStrains.set(strain, 500)
        }
    }
}