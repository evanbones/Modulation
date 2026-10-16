plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") version "1.18.2" apply false
    id("net.neoforged.moddev") version "2.0.147" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.2.1" apply false
    id("org.moddedmc.wiki.toolkit") version "0.4.1"
}

stonecutter active "1.21.1-neoforge"

stonecutter parameters {
    val loader = node.metadata.project.substringAfterLast('-')
    constants.match(loader, "fabric", "neoforge")
    fun rename(from: String, to: String) = listOf("(?<![\"-])\\b$from\\b(?!\")", to, "(?<![\"-])\\b$to\\b(?!\")", from)
    replacements {
        regex(eval(node.metadata.version, ">=26.1")) {
            for ((a, b) in listOf(
                "ResourceLocation" to "Identifier",
                "ResourceLocationPattern" to "IdentifierPattern",
                "GuiGraphics" to "GuiGraphicsExtractor",
                "ClickType" to "ContainerInput",
                "MobSpawnType" to "EntitySpawnReason",
                "UseAnim" to "ItemUseAnimation",
            )) {
                val (p1, r1, p2, r2) = rename(a, b)
                replace(p1, r1, p2, r2)
            }
            replace(
                "(?<=\\bBuiltInRegistries\\.\\w{1,40})\\.get\\(", ".getValue(",
                "(?<=\\bBuiltInRegistries\\.\\w{1,40})\\.getValue\\(", ".get("
            )
        }
        regex(eval(node.metadata.version, ">=26.2")) {
            replace("(?<!\\.gui)\\.setScreen\\(", ".gui.setScreen(", "\\.gui\\.setScreen\\(", ".setScreen(")
            replace("\\.gui\\.getChat\\(\\)", ".gui.hud.getChat()", "\\.gui\\.hud\\.getChat\\(\\)", ".gui.getChat()")
            replace(
                "(?<=\\b(?:minecraft|mc|getInstance\\(\\)))\\.screen\\b(?!\\()", ".gui.screen()",
                "\\.gui\\.screen\\(\\)", ".screen"
            )
        }
        string(eval(node.metadata.version, ">=26.1")) {
            replace("import net.minecraft.Util;", "import net.minecraft.util.Util;")
            for ((pkg, cls) in listOf(
                "net.minecraft.world.entity.animal" to "bee.Bee",
                "net.minecraft.world.entity.projectile" to "arrow.ThrownTrident",
                "net.minecraft.world.entity.projectile" to "throwableitemprojectile.ThrownEnderpearl",
                "net.minecraft.world.entity.vehicle" to "minecart.MinecartFurnace",
                "net.minecraft.world.entity.vehicle" to "minecart.AbstractMinecart",
                "net.minecraft.world.entity.vehicle" to "boat.Boat",
                "net.minecraft.client.renderer.block.model" to "geometry.BakedQuad",
            )) {
                val simple = cls.substringAfterLast('.')
                val newPkg = if (cls.startsWith("geometry.")) "net.minecraft.client.resources.model" else pkg
                replace("import $pkg.$simple;", "import $newPkg.$cls;")
            }
        }
        string(eval(node.metadata.version, ">=26.3")) {
            for ((old, new) in listOf(
                "com.mojang.blaze3d.pipeline.BlendFunction" to "com.mojang.renderpearl.api.pipeline.BlendFunction",
                "com.mojang.blaze3d.pipeline.ColorTargetState" to "com.mojang.renderpearl.api.pipeline.ColorTargetState",
                "com.mojang.blaze3d.pipeline.RenderPipeline" to "com.mojang.renderpearl.api.pipeline.RenderPipeline",
                "com.mojang.blaze3d.pipeline.BindGroupLayout" to "com.mojang.renderpearl.api.pipeline.BindGroupLayout",
                "com.mojang.blaze3d.PrimitiveTopology" to "com.mojang.renderpearl.api.pipeline.PrimitiveTopology",
                "com.mojang.blaze3d.GpuFormat" to "com.mojang.renderpearl.api.GpuFormat",
                "com.mojang.blaze3d.systems.CommandEncoder" to "com.mojang.renderpearl.api.commands.CommandEncoder",
                "com.mojang.blaze3d.systems.RenderPass" to "com.mojang.renderpearl.api.commands.RenderPass",
                "com.mojang.blaze3d.buffers.GpuBufferSlice" to "com.mojang.renderpearl.api.buffers.GpuBufferSlice",
                "com.mojang.blaze3d.textures.FilterMode" to "com.mojang.renderpearl.api.textures.FilterMode",
                "com.mojang.blaze3d.textures.AddressMode" to "com.mojang.renderpearl.api.textures.AddressMode",
                "com.mojang.blaze3d.textures.GpuSampler" to "com.mojang.renderpearl.api.textures.GpuSampler",
                "com.mojang.blaze3d.textures.GpuTexture;" to "com.mojang.renderpearl.api.textures.GpuTexture;",
                "com.mojang.blaze3d.textures.GpuTextureView" to "com.mojang.renderpearl.api.textures.GpuTextureView",
            )) {
                replace("import $old", "import $new")
            }
        }
    }
}

stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}

for (version in stonecutter.versions.map { it.version }.distinct()) tasks.register("publish$version") {
    group = "publishing"
    dependsOn(stonecutter.tasks.named("publishMods") { metadata.version == version })
}

wiki {
    wikiAccessToken = System.getenv("WIKI_ACCESS_TOKEN")
    docs.create("modulation") {
        root = file("docs/")
    }
}
