# =============================================================================
# ROLEORA - ProGuard & R8 Optimization Rules for Release Build
# =============================================================================

# Preserve source line numbers for clean production stack traces
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Room database and DAO implementations
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep Kotlin Coroutines & Flow reflection mechanisms
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep Moshi JSON models and generated adapters
-keepclasseswithmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep class com.squareup.moshi.** { *; }
-keep class * extends com.squareup.moshi.JsonAdapter { *; }

# Keep Firebase Firestore and Auth model classes
-keepattributes *Annotation*
-keepclassmembers class com.example.roleora.data.model.** {
    !static <fields>;
    public <methods>;
}
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Android Jetpack Compose runtime rules
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.**
