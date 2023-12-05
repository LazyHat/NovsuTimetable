// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.tracer) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

//defaultConfig {
//    pluginToken = "edYkiAjuFObdxOunWDMIzzYXpaWNozQnvV8ThqIdVp81"
//    appToken = "N6l6o3QG81hAdGgoHPjkPFw8PEGSkBGP89IaoSVCWL8"
//}
true // Needed to make the Suppress annotation work for the plugins block