object Main {
    @JvmStatic fun main(args: Array<String>) {

        //System.out.println((0..5).toList())

        System.out.println("Initializing transcription dictionary...")
        initialize()

        System.out.println("Generating world...")
        val world = World(1.0)
        world.generateCommunities(3, 0.2, 1.0, 20, 100)
        val startPopulation = world.getPopulation()
        System.out.println("Population: $startPopulation")
        System.out.println("Introducing virus...")
        world.introducePathogens(15)

        System.out.println("Beginning simulation...")
        val iterations = 100
        val epochLength = 5
        var epoch = 1
        for (i in 0..iterations) {
            world.update()
            if (i % epochLength == 0) {
                System.out.println("Epoch " + epoch++ + " complete")
                //world.communities[0].individuals[0].getStatus()
                val viruses = world.getVirusCount()
                System.out.println("Total viruses (world): " + viruses)
                val population = world.getPopulation()
                System.out.println("Remaining population before regeneration: " + population)

                if (population.toDouble() / startPopulation.toDouble() < 0.5) {
                    System.out.println("Regenerating populations...")
                    for (community in world.communities) {
                        community.regenerate()
                    }
                    System.out.println("Population after regeneration: " + world.getPopulation())
                }

                if (viruses[1] <= 40) {
                    System.out.println("Introducing more pathogens...")
                    world.introducePathogens(15)
                }

                // calculate kmeans
                val inputStrainsMutable = mutableListOf<List<Double>>()
                world.viruses.entries.forEach({ (strain, count) ->
                    for (i in 0 until count) {
                        inputStrainsMutable.add(encodeSequence(strain).map({ it.toDouble() }))
                    }
                })

                val inputStrains = inputStrainsMutable.toList()
                val centroidsAndClusterSizes = onlineKMeans(dimensions=numberOfFeatures,
                    k=3,
                    input=world.viruses
                )

                System.out.println(world.viruses)
                System.out.println(centroidsAndClusterSizes)

                System.out.println("")

                System.gc()

            }
        }

    }
}