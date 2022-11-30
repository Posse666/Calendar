# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

-dontwarn android.telephony.HwTelephonyManager

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
	public static void checkNotNull(...);
	public static void checkExpressionValueIsNotNull(...);
	public static void checkNotNullExpressionValue(...);
	public static void checkParameterIsNotNull(...);
	public static void checkNotNullParameter(...);
	public static void checkReturnedValueIsNotNull(...);
	public static void checkFieldIsNotNull(...);
	public static void throwUninitializedPropertyAccessException(...);
	public static void throwNpe(...);
	public static void throwJavaNpe(...);
	public static void throwAssert(...);
	public static void throwIllegalArgument(...);
	public static void throwIllegalState(...);
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile