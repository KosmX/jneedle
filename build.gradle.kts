plugins {
    alias(libs.plugins.axion.release)
}

allprojects {
    group = "dev.kosmx.needle"
    version = rootProject.scmVersion.version
}
