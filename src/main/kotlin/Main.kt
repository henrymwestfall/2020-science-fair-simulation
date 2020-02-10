object Main {
    @JvmStatic fun main(args: Array<String>) {
        val points = listOf(
                listOf(-5.0, -5.0),
                listOf(-3.0, -4.0),
                listOf(-2.0, -6.0),
                listOf(-5.0, -3.0),
                listOf(-2.0, -3.0),
                listOf(5.0, 5.0),
                listOf(3.0, 4.0),
                listOf(2.0, 6.0),
                listOf(5.0, 3.0),
                listOf(6.0, 3.0)
        )
        System.out.println(calculateCentroid(2, points))
        System.out.println(kmeans(dimensions=2, clusters=2, input=points, tolerance=0.05))

        //System.out.println((0..5).toList())
        /*
        System.out.println("Initializing transcription dictionary...")
        initializeTranscriptionMaps()

        System.out.println("Generating world...")
        val world = World(1.0)
        world.generateCommunities(5, 0.0, 1.0, 0, 500)
        System.out.println("Introducing virus...")
        world.introducePathogens(1)

        System.out.println("Beginning simulation...")
        val iterations = 1000
        val epochLength = 10
        var epoch = 1
        for (i in 0..iterations) {
            world.update()
            if (i % epochLength == 0) {
                System.out.println("Epoch " + epoch++ + " complete")
                world.communities[0].individuals[0].getStatus()
                System.out.println("Total viruses (world): " + world.getVirusCount())
                System.out.println("")
            }
        }
        System.out.println("Remaining population: " + world.communities[0].individuals.size)*/

    }
}