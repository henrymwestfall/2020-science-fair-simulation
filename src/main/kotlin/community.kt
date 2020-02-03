import java.util.*

class Community(val connectivity: Double, val population: Int) {
    var id = -1

    var nextID = 0
    val individuals = mutableListOf<Individual>()

    val neighbors = mutableListOf<Community>()

    fun setID(to: Int) {
        id = to
    }

    fun addIndividual(newIndividual: Individual) {
        // maybe connect to other individuals
        newIndividual.setID(nextID++)
        for (individual in individuals) {
            if (rand.nextDouble() <= connectivity) {
                individual.connect(newIndividual)
                newIndividual.connect(individual)
            }
        }

        individuals.add(newIndividual)
    }

    fun connect(to: Community) {
        neighbors.add(to)
    }

    fun generateIndividuals() {
        for (i in 0..population) {
            val individual = Individual()
            addIndividual(individual)
        }
    }

    fun update() {
        for (individual in individuals) {
            if (individual.dead) {
                continue
            }
            individual.update()
        }
    }

    fun getVirusCount(): Int {
        var count = 0
        for (individual in individuals) {
            if (!individual.dead) {
                count += individual.getTotalVirusCount()
            }
        }
        return count
    }
}