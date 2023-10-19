pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://artifactory-external.vkpartner.ru/artifactory/maven/")}
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://artifactory-external.vkpartner.ru/artifactory/maven/") }
    }
}

rootProject.name = "NovsuTimeTable"
include(":app")
