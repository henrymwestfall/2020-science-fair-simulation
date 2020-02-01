object Main {
    @JvmStatic fun main(args: Array<String>) {
        System.out.println("Generating world...")
        val world = World(1.0)
        world.generateCommunities(20, 0.0, 1.0, 50, 1000)

        System.out.println("Beginning simulation...")
        val iterations = 1000
        for (i in 0..iterations) {
            world.update()
        }

    }
}