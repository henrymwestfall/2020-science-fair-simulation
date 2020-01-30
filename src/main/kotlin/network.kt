import java.util.*

class Network<Class>(connectionProb: Double) {
    // a general network class
    val nodes = mutableListOf<Class>()
    val connectionProb = connectionProb
    val connections = hashMapOf<Class, MutableList<Class>>()

    var nextID = 0

    private fun createNode() {
        // TODO - waiting for StackOverflow help
    }
}