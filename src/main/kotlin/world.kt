import java.util.*

class World(connectionProb: Double) {
    var nextID = 0
    val communities = mutableListOf<Community>()
    val connectionProb = connectionProb

    fun addCommunity(newCommunity: Community) {
        // maybe connect to other communities
        newCommunity.setID(nextID++)
        for (community in communities) {
            if (rand.nextDouble() <= connectionProb) {
                community.connect(newCommunity)
                newCommunity.connect(community)
            }
        }

        communities.add(newCommunity)
    }
}