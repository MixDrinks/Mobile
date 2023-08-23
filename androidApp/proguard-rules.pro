-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.slf4j.impl.StaticLoggerBinder

# Don't note a bunch of dynamically referenced classes
-dontnote com.google.**
-dontwarn com.google.firebase.messaging.**
-keep public class com.google.firebase.**
-keep class com.google.android.gms.internal.**
-keepclasseswithmembers class com.google.firebase.FirebaseException
-dontnote com.squareup.okhttp.**
-dontnote okhttp3.internal.**

# Recommended flags for Firebase Auth
-keepattributes Signature
-keepattributes *Annotation*

# Retrofit config
-dontnote retrofit2.Platform
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Exceptions

# TODO remove https://github.com/google/gson/issues/1174
-dontwarn com.google.gson.Gson$6

