import kotlin.math.sqrt
import kotlin.Double.Companion.POSITIVE_INFINITY

fun calculateDistance(a: List<Double>, b: List<Double>): Double {
    /** Calculate the distance between point a and b with any number of dimensions **/
    val differences = (0..a.size-1).toList().map { a[it] - b[it] }
    val squares = differences.map { it * it }
    val sum = squares.sum()
    return sqrt(sum)
}

fun calculateCentroid(dimensions: Int, points: List<List<Double>>): List<Double> {
    /** Calculate the centroid of points with any number of dimensions
     *  For example, if points = listOf(listOf(5.0, 3.0), listOf(9.0, 6.0), listOf(10.0, 6.0)),
     *  this function will output [8.0, 5.0]
     * **/
    //val dimensions = points[0].size
    val sums = mutableListOf<Double>(*(1..dimensions).toList().map { 0.0 }.toTypedArray())
    for (point in points) {
        point.forEachIndexed { index, coordinate ->
            sums.set(index, sums[index] + coordinate)
        }
    }

    val hm_points = points.size
    val centroid = sums.map { it / hm_points }
    return centroid
}

fun kmeans(dimensions: Int, clusters: Int, input: List<List<Double>>, iterations: Int = -1, tolerance: Double = 0.0): HashMap<List<Double>, Int> {
    /** Apply the kmeans algorithm to data points
     *  dimensions  -- how many dimensions each data point has
     *  clusters    -- how many clusters to find
     *  points      -- the data points to analyze
     *  iterations  -- the number of iterations to run the simulation for. If negative, there is no limit
     *  tolerance   -- the maximum accepted movement of centroids between iterations to be considered settled**/

    val centroids = mutableListOf<List<Double>>()
    val centroidCache = mutableListOf<List<Double>>()
    for (i in 1..clusters) {
        val centroid = (1..dimensions).toList().map { rand.nextInt(3 * clusters).toDouble() } // random initial centroids
        centroids.add(centroid)
        centroidCache.add(centroid)
    }

    val groups = (1..clusters).toList().map { mutableListOf<List<Double>>() }

    var currentIteration = 0
    var settled = false

    // begin algorithm
    while ((currentIteration < iterations) || !settled) {
        ++currentIteration
        //System.out.println("\nIteration: $currentIteration\n")

        /* allocate points */
        //System.out.println("Allocating points")
        for (group in groups) {
            group.clear()
        }
        for (point in input) {
            var closest = centroids[0]
            var shortestDistance = POSITIVE_INFINITY
            for (centroid in centroids) {
                val distance = calculateDistance(point, centroid)
                if (distance < shortestDistance) {
                    shortestDistance = distance
                    closest = centroid
                }
            }

            val centroidIndex = centroids.indexOf(closest)
            groups[centroidIndex].add(point)
        }

        //System.out.println("Calculating new centroids")
        // calculate new centroids
        centroids.forEachIndexed( { index, _ ->
            val group = groups[index]
            val centroid = calculateCentroid(dimensions, group)
            centroids.set(index, centroid)
        })
        //System.out.println("Centroids: $centroids")

        // compare to old centroids
        if (currentIteration == 1) {
            // skip if it's the first iteration because there is nothing to compare
            continue
        }

        //System.out.println("Comparing to old centroids")
        settled = true
        for ((centroid, cached) in centroids zip centroidCache) {
            val distance = calculateDistance(centroid, cached)
            if (distance > tolerance) {
                // if distance exceeds the tolerance, centroids have not settled
                settled = false
                break
            }
        }

        centroidCache.forEachIndexed( { index, _ ->
            centroidCache.set(index, centroids[index])
        })
    }

    val centroidsAndClusterSizes = HashMap<List<Double>, Int>()
    for ((centroid, group) in centroids zip groups) {
        centroidsAndClusterSizes.set(centroid, group.size)
    }

    return centroidsAndClusterSizes
}