const val RELEASE_USER = "hendraanggrian"
const val RELEASE_GROUP = "com.$RELEASE_USER."
const val RELEASE_ARTIFACT = "buildconfig-gradle-plugin"
const val RELEASE_VERSION = "0.5-rc1"
const val RELEASE_DESC = "BuildConfig gradle plugin for Java projects"
const val RELEASE_WEBSITE = "https://github.com/$RELEASE_USER/$RELEASE_ARTIFACT"

val BINTRAY_USER = System.getenv("BINTRAY_USER")
val BINTRAY_KEY = System.getenv("BINTRAY_KEY")