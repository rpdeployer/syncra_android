#######################################
## Android core components
#######################################
# Сохраняем Activity, Service, BroadcastReceiver, ContentProvider
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Сохраняем Application
-keep public class * extends android.app.Application

#######################################
## Kotlin / Coroutines
#######################################
-dontwarn kotlin.**
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }

#######################################
## Jetpack Compose
#######################################
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }
-dontwarn androidx.compose.**

#######################################
## Hilt / Dagger
#######################################
-keep class dagger.** { *; }
-keep interface dagger.** { *; }
-dontwarn dagger.**

-keep class javax.inject.** { *; }
-keep interface javax.inject.** { *; }
-dontwarn javax.inject.**

-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

#######################################
## Retrofit / OkHttp / Gson
#######################################
# Retrofit annotations
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# Gson models (сохраняем все поля моделей с аннотацией @SerializedName)
-keep class com.google.gson.annotations.SerializedName
-keepattributes *Annotation*

-keep class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

#######################################
## Room
#######################################
# Room Entities & DAO
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.* class *
-keep interface * implements androidx.room.*Dao

#######################################
## Serialization (если используешь Serializable)
#######################################
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    private void readObjectNoData();
}

#######################################
## Logging (по желанию — чтобы Timber работал)
#######################################
-dontwarn timber.log.**
-keep class timber.log.** { *; }
