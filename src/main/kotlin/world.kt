import java.util.*

class World(val connectivity: Double) {
    var nextID = 0
    val communities = mutableListOf<Community>()
    val viruses = HashMap<String, Long>()

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
            viruses.clear()
            community.viruses.entries.forEach({ (strain, count) ->
                viruses.set(strain, (viruses.get(strain) ?: 0) + count)
            })
        }
    }

    fun getVirusCount(): List<Long> {
        var count = 0L
        var strains = 0L
        for (community in communities) {
            val counts = community.getVirusCount()
            strains += counts[0]
            count += counts[1]
        }
        return listOf(strains, count)
    }

    fun getPopulation(): Int {
        var population = 0
        communities.forEach({ community ->
            population += community.getLivingPopulation()
        })
        return population
    }

    fun introducePathogens(count: Int) {
        // Infect some individuals randomly
        for (i in 0..count) {
            val strain = universalVulnerability
            val targetCommunity = communities[rand.nextInt(communities.size - 1)]
            val targetIndividual = targetCommunity.individuals[rand.nextInt(communities.size - 1)]
            targetIndividual.contractedStrains.set(strain, 10)
        }
    }
}