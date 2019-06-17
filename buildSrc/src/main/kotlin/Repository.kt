import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import java.net.URI

fun RepositoryHandler.mainRepos() {
    google()
    mavenCentral()
    jcenter()
}

fun RepositoryHandler.jitpack(): MavenArtifactRepository =
    maven { url = URI("https://jitpack.io") }
