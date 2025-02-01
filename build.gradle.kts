plugins {
    java
    application
}

group = "com.geodetskialati"
version = "1.0-SNAPSHOT"

repositories {
    // Postojeći flatDir repozitorijum za lokalne JAR fajlove
    flatDir {
        dirs("libs")
    }
    mavenCentral() // Maven Central za preuzimanje Proj4j i drugih biblioteka
}

dependencies {
    // Lokalni JAR fajlovi
    implementation(files("libs/opencsv-5.6.jar"))
    implementation(files("libs/commons-lang3-3.12.0.jar"))
    implementation(files("libs/poi-4.1.2.jar"))
    implementation(files("libs/poi-ooxml-4.1.2.jar"))
    implementation(files("libs/commons-collections4-4.1.jar"))
    implementation(files("libs/xmlbeans-3.1.0.jar"))
    implementation(files("libs/commons-compress-1.20.jar"))
    implementation(files("libs/poi-ooxml-schemas-4.1.2.jar"))

    // Dodavanje Proj4j biblioteke
    implementation(files("libs/proj4j-1.1.0.jar"))  // Verzija 1.1.0 je stabilna

    // JUnit za testiranje
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

application {
    mainClass.set("com.geodetskialati.Main")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
