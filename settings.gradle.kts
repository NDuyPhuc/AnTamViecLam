pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // If you need to add a custom Maven repository, you would add it here like this:
        // maven {
        //     url = uri("https://your.custom.repository/maven2/")
        // }
    }
}
rootProject.name = "AnTamViecLam"
include(":app")
