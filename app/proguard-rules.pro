# Keep data model classes used with Gson for JSON deserialization
-keep class app.krafted.volcanotimeline.data.model.** { *; }

# Keep Room entity and DAO classes
-keep class app.krafted.volcanotimeline.data.db.** { *; }

# Gson requirements
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Preserve stack traces in crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
