import java.io.File

object TestFileIO {
    @JvmStatic fun main(args: Array<String>) {
        File("/home/roboticsloaner/Documents/projects/2020-science-fair-simulation/src/main/kotlin/test.txt").printWriter().use { out ->
            out.println("First line")
            out.println("Second line")
        }
    }
}