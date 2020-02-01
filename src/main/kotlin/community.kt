import java.util.*

class Community(connectionProb: Double) {
    var id = -1

    var nextID = 0
    val individuals = mutableListOf<Individual>()
    val connectionProb = connectionProb

    val neighbors = mutableListOf<Community>()

    public fun setID(to: Int) {
        id = to
    }

    public fun addIndividual(newIndividual: Individual) {
        // maybe connect to other individuals
        newIndividual.setID(nextID++)
        for (individual in individuals) {
            if (rand.nextDouble() <= connectionProb) {
                individual.connect(newIndividual)
                newIndividual.connect(individual)
            }
        }

        individuals.add(newIndividual)
    }

    public fun connect(to: Community) {
        neighbors.add(to)
    }
}