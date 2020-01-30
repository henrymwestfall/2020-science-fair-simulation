object Main {
    @JvmStatic fun main(args: Array<String>) {
        val indv = Individual(5)
        indv.getStatus()

        val network = Network<Individual>(0.5)
    }
}