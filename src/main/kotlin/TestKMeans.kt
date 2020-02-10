object TestKMeans {
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
        System.out.println("Centroid: ${calculateCentroid(2, points)}")
        System.out.println("KMeans Result: ${kmeans(dimensions=2, clusters=2, input=points, tolerance=0.05)}")
    }
}