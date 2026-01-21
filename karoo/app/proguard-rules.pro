# Karoo Extension
-keep class io.hammerhead.karooext.** { *; }

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn io.ktor.util.KtorDsl
-dontwarn io.ktor.utils.io.core.ByteReadPacket
-dontwarn io.ktor.utils.io.core.Input
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.tribly.karoo.**$$serializer { *; }
-keepclassmembers class com.tribly.karoo.** {
    *** Companion;
}
-keepclasseswithmembers class com.tribly.karoo.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ZXing
-keep class com.google.zxing.** { *; }
