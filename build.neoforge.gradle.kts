plugins {
    id("net.neoforged.moddev")
    id("me.modmuss50.mod-publish-plugin")
    id("maven-publish")
}

val minecraft = stonecutter.current.version
val mcVersion = stonecutter.current.project.substringBeforeLast('-')
fun dep(name: String) = property("deps.$name") as String
fun optDep(name: String) = findProperty("deps.$name") as String?
fun since(predicate: String) = stonecutter.eval(minecraft, predicate)

val modId = property("mod.id") as String

for ((dir, predicate) in RESOURCE_OVERLAYS) {
    if (since(predicate)) sourceSets.main { resources.srcDir(rootProject.file("src/main/overlays/$dir")) }
}

tasks.named<ProcessResources>("processResources") {
    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>().apply {
        this["version"] = prop("mod.version") + "+" + prop("deps.minecraft")
        this["minecraft_version_range"] = prop("mod.mc_dep_forgelike")
        this["mod_id"] = prop("mod.id")
        this["mod_name"] = prop("mod.name")
        this["description"] = prop("mod.description")
        this["mod_author"] = prop("mod.author")
        this["credits"] = prop("mod.credits")
        this["license"] = prop("mod.license")
        this["neoforge_loader_version_range"] = prop("deps.neoforge_loader_version_range")
        this["neoforge_version"] = prop("deps.neoforge")
        this["java_version"] = prop("deps.java_version")
        this["yacl_version"] = prop("deps.yacl").substringBefore('+')
    }
    inputs.properties(props)

    filesMatching(listOf("pack.mcmeta", "META-INF/neoforge.mods.toml", "*.mixins.json")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${property("deps.minecraft")}-neoforge"
base.archivesName = modId

repositories {
    modulationRepositories()
}

neoForge {
    enable {
        version = dep("neoforge")
        isDisableRecompilation = true
    }
    validateAccessTransformers = true

    val at = rootProject.file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) accessTransformers.from(at.absolutePath)

    optDep("parchment")?.let {
        parchment {
            minecraftVersion = dep("minecraft")
            mappingsVersion = it
        }
    }

    runs {
        configureEach {
            systemProperty("neoforge.warnings.onlyin.hide", "true")
        }
        register("client") {
            gameDirectory = file("run/client")
            client()
        }
        register("server") {
            gameDirectory = file("run/server")
            server()
        }
    }

    mods {
        register(modId) {
            sourceSet(sourceSets["main"])
        }
    }
}

tasks {
    processResources {
        exclude(
            "**/fabric.mod.json", "**/*.accesswidener", "**/*.classtweaker", "**/mods.toml",
            "**/*.fabric.mixins.json"
        )
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

val localRuntime: Configuration = configurations.create("localRuntime")
configurations.runtimeClasspath { extendsFrom(localRuntime) }

dependencies {
    // YACL
    implementation("maven.modrinth:yacl:${dep("yacl")}")

    // Mixin Constraints
    implementation("com.moulberry:mixinconstraints:${dep("mixinconstraints")}")
    "jarJar"("com.moulberry:mixinconstraints:[${dep("mixinconstraints")},)") {
        version { prefer(dep("mixinconstraints")) }
    }

    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")

    // EMI
    optDep("emi")?.let {
        compileOnly("dev.emi:emi-neoforge:$it")
        localRuntime("dev.emi:emi-neoforge:$it")
    }
    optDep("remi")?.let { localRuntime("maven.modrinth:reliable-emi:$it-neoforge") }

    // JEI
    optDep("jei")?.let { compileOnly("mezz.jei:jei-${it.substringBefore(':')}:${it.substringAfter(':')}") }

    // Figura
    optDep("figura")?.let { compileOnly("maven.modrinth:figura:$it") }

    // Reconnectible Chains
    optDep("reconnectible_chains")?.let { compileOnly("maven.modrinth:reconnectible-chains:$it") }
    optDep("cloth_config")?.let { compileOnly("maven.modrinth:cloth-config:$it") }

    // Sodium
    optDep("sodium")?.let {
        compileOnly("net.caffeinemc:sodium-neoforge:$it")
        compileOnly("net.caffeinemc:sodium-neoforge-mod:$it")
        if (!since(">=26.1")) compileOnly("net.caffeinemc:sodium-neoforge-api:$it")
    }

    // Vanilla Backport
    optDep("vanillabackport")?.let {
        compileOnly("maven.modrinth:vanillabackport:$it")
        localRuntime("maven.modrinth:vanillabackport:$it")
    }
    optDep("platform")?.let { localRuntime("maven.modrinth:platform:$it") }

    // Farmer's Delight
    optDep("farmersdelight")?.let {
        val artifact = if (since(">=26.1")) "farmers-delight-refabricated" else "farmers-delight"
        compileOnly("maven.modrinth:$artifact:$it") { isTransitive = false }
    }

    // Dyed Flames
    optDep("dyed_flames")?.let { compileOnly("maven.modrinth:dyed-flames:$it") { isTransitive = false } }
    optDep("puzzles_lib")?.let { compileOnly("maven.modrinth:puzzles-lib:$it") { isTransitive = false } }
}

val javaVer = (property("deps.java_version") as String).toInt()
java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVer)
    sourceCompatibility = JavaVersion.toVersion(javaVer)
    targetCompatibility = JavaVersion.toVersion(javaVer)
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = javaVer
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "5000"))
}

val modName = property("mod.name") as String
tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE")) {
        rename("LICENSE", "LICENSE_$modName")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = property("mod.group") as String
            artifactId = "$modId-neoforge"
            version = "${property("mod.version")}+${property("deps.minecraft")}"

            from(components["java"])
        }
    }
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()

publishMods {
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

    type = STABLE
    displayName = "${property("mod.name")} NeoForge $mcVersion - ${property("mod.version")}"
    version = "${property("mod.version")}+${property("deps.minecraft")}-neoforge"
    changelog = provider { rootProject.file("CHANGELOG-LATEST.md").readText() }
    modLoaders.add("neoforge")

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = providers.environmentVariable("MODRINTH_TOKEN").orElse(providers.environmentVariable("MODRINTH_API_KEY"))
        minecraftVersions.add(property("deps.minecraft") as String)
        minecraftVersions.addAll(additionalVersions)
        optional("yacl")
        optional("reconnectible-chains")
        if (!since(">=26.1")) optional("figura")
    }

    curseforge {
        projectId = property("publish.curseforge") as String
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN").orElse(providers.environmentVariable("CURSEFORGE_API_KEY"))
        minecraftVersions.add(property("deps.minecraft") as String)
        minecraftVersions.addAll(additionalVersions)
        optional("yacl")
        optional("reconnectible-chains")
        if (!since(">=26.1")) optional("figura")
        client = true
        server = true
    }
}
