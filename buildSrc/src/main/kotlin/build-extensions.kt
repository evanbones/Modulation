import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

val RESOURCE_OVERLAYS: Map<String, String> = linkedMapOf(
    "pre-26.1" to "<26.1",
    "26.x" to ">=26.1",
    "26.1-26.2" to ">=26.1 <26.3",
    "26.3" to ">=26.3",
)

fun accessWidenerName(modId: String, unobfuscated: Boolean) =
    if (unobfuscated) "$modId.classtweaker" else "$modId.accesswidener"

fun RepositoryHandler.modulationRepositories() {
    mavenCentral()
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
    maven("https://maven.terraformersmc.com/releases/") {
        name = "Terraformers (Mod Menu, EMI)"
        content {
            includeGroupAndSubgroups("com.terraformersmc")
            includeGroupAndSubgroups("dev.emi")
        }
    }
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven (YACL)"
        content {
            includeGroupAndSubgroups("dev.isxander")
            includeGroupAndSubgroups("org.quiltmc.parsers")
        }
    }
    maven("https://maven.quiltmc.org/repository/release/") {
        name = "Quilt Maven"
        content { includeGroupAndSubgroups("org.quiltmc.parsers") }
    }
    maven("https://maven.caffeinemc.net/releases") {
        name = "CaffeineMC Releases (Sodium)"
        content { includeGroupAndSubgroups("net.caffeinemc") }
    }
    maven("https://maven.caffeinemc.net/snapshots") {
        name = "CaffeineMC Snapshots (Sodium)"
        content { includeGroupAndSubgroups("net.caffeinemc") }
    }
    maven("https://maven.blamejared.com") {
        name = "BlameJared (JEI)"
        content { includeGroupAndSubgroups("mezz.jei") }
    }
    maven("https://maven.parchmentmc.org") {
        name = "ParchmentMC"
        content { includeGroupAndSubgroups("org.parchmentmc") }
    }
}
