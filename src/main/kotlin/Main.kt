object Main {
    @JvmStatic fun main(args: Array<String>) {
        System.out.println("Initializing transcription dictionary...")
        initializeTranscriptionMaps()

        System.out.println("Generating world...")
        val world = World(1.0)
        world.generateCommunities(1, 0.0, 1.0, 0, 100)
        System.out.println("Introducing virus...")
        world.introducePathogens(1)

        System.out.println("Beginning simulation...")
        val iterations = 1000
        val epochLength = 50
        var epoch = 1
        for (i in 0..iterations) {
            world.update()
            if (i % epochLength == 0) {
                System.out.println("Epoch " + epoch++ + " complete")
                world.communities[0].individuals[0].getStatus()
                System.out.println("Total viruses (community): " + world.communities[0].getVirusCount())
                System.out.println("")
            }
        }
        System.out.println("Remaining population: " + world.communities[0].individuals.size)

    }
}