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
}