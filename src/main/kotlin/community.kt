import java.util.*

class Community(val connectivity: Double, val population: Int) {
    var id = -1

    var nextID = 0
    val individuals = mutableListOf<Individual>()

    val neighbors = mutableListOf<Community>()

    val viruses = HashMap<String, Long>()

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

    fun transmit() {
        if (rand.nextDouble() > 0.5) {
            neighbors.forEach({ neighbor ->
                individuals.shuffle()
                var selected = listOf<Individual>()
                try {
                    selected = individuals.slice(0..5)
                } catch (e: java.lang.IndexOutOfBoundsException) {
                    selected = individuals
                }
                var targets = listOf<Individual>()
                try {
                    targets = neighbor.individuals.shuffled().slice(0..5)
                } catch (e: java.lang.IndexOutOfBoundsException) {
                    targets = neighbor.individuals
                }
                (selected zip targets).forEach({ (individual, target) ->
                    if (!individual.dead || individual.complications > 0) {
                        individual.contractedStrains.keys.forEach({ strain ->
                            target.contractedStrains.set(strain, (individual.contractedStrains.get(strain) ?: 0) + 1)
                        })
                    }
                })
            })
        }
    }

    fun generateIndividuals() {
        for (i in 0..population) {
            val individual = Individual()
            addIndividual(individual)
        }
    }

    fun regenerate() {
        val numberOfIndividuals = individuals.filter({!it.dead}).size
        for (i in 0..((population - numberOfIndividuals) * 0.8).toInt()) {
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
            viruses.clear()
            individual.contractedStrains.entries.forEach({ (strain, count) ->
                viruses.set(strain, (viruses.get(strain) ?: 0) + 1)
            })
        }

        transmit()
    }

    fun getVirusCount(): List<Long> {
        var count = 0L
        var strains = 0L
        for (individual in individuals) {
            if (!individual.dead) {
                count += individual.getTotalVirusCount()
                strains += individual.contractedStrains.keys.size.toLong()
            }
        }
        return listOf(strains, count)
    }

    fun getLivingPopulation(): Int {
        val living = individuals.filter({ !it.dead })
        return living.size
    }
}