allprojects {
    tasks.withType<Test> {
        useJUnitPlatform()
    }
    dependencies {
        testImplementation(rootProject.hexalite.bundles.testing)
        api(project(":kraken:purpur"))
    }
}