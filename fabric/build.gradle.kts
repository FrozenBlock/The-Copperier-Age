plugins {
    id("net.frozenblock.triangle.fabric")
    id("org.quiltmc.gradle.licenser")
    checkstyle
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    toolVersion = "10.20.2"
}

val githubActions: Boolean = System.getenv("GITHUB_ACTIONS") == "true"
val licenseChecks: Boolean = githubActions

val fabric_loader_version: String by project
val min_fabric_loader_version: String by project

val mod_id: String by project
val mod_version: String by project
val minecraft_version: String by project
val maven_group: String by project
val archives_base_name: String by project

val fabric_api_version: String by project
val frozenlib_version: String by project

val modmenu_version: String by project
val cloth_config_version: String by project
val audioplayer_version: String by project
val voicechat_api_version: String by project

base {
    archivesName = archives_base_name
}

val release = findProperty("releaseType") == "stable"

version = getModVersion()
group = maven_group

tasks.jar {
    archiveClassifier.set("fabric")
}

fabric {
    dependOn(project(":tca-common"))
    accessWidener(project(":tca-common"))
    dataGen {
        owner = project(":tca-common")
        splitSourceSet("datagen")
    }
}

loom {
    enableTransitiveAccessWideners = true
    interfaceInjection {
        enableDependencyInterfaceInjection = true
    }

    runs {
        named("client") {
            vmArg("-DMC_DEBUG_FROZENLIB_WIND_DISTURBANCES=true")
            vmArg("-DMC_DEBUG_ENABLED=true")
            vmArg("-DMC_DEBUG_FROZENLIB_WIND=true")
        }
    }
}

repositories {
    flatDir {
        dirs("libs")
    }
}

dependencies {
    // FrozenLib
    api("net.frozenblock:frozenlib-fabric:${frozenlib_version}")

    // Mod Menu
    compileOnly("maven.modrinth:modmenu:$modmenu_version")

    // Cloth Config
    compileOnly("me.shedaniel.cloth:cloth-config-fabric:$cloth_config_version") {
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "com.terraformersmc")
    }

    compileOnly("maven.modrinth:audioplayer:$audioplayer_version")
    compileOnly("de.maxhenkel.voicechat:voicechat-api:$voicechat_api_version")
}

tasks {
    license {
        if (licenseChecks) {
            rule(rootProject.file("codeformat/HEADER"))

            include("**/*.java")
        }
    }
}

val applyLicenses: Task by tasks
val test: Task by tasks
val runClient: Task by tasks

val sourcesJar: Jar by tasks
val javadocJar: Jar by tasks

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}

fun getModVersion(): String {
    var version = "$mod_version-mc$minecraft_version"

    if (!release) {
        version += "-unstable"
    }

    return version
}

val changelogText = run {
    val split = rootProject.file("CHANGELOG.md").readText().split("-----------------")
    check(split.size == 2) { "Malformed changelog" }
    split[1].trim()
}

upload {
    maven {
        name.set("thecopperierage-fabric")
    }

    forEach {
        changelog = changelogText
    }

    modrinth {
        dependencies {
            required("fabric-api")
            required("frozenlib")
            optional("cloth-config")
            optional("modmenu")
            optional("simple-copper-pipes")
            optional("glowtone")
        }
    }
}
