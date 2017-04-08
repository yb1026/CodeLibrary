###################################################
#common
###################################################
# Optimizations: If you don't want to optimize, use the
# proguard-android.txt configuration file instead of this one, which
# turns off the optimization flags.  Adding optimization introduces
# certain risks, since for example not all optimizations performed by
# ProGuard works on all versions of Dalvik.  The following flags turn
# off various optimizations known to have issues, but the list may not
# be complete or up to date. (The "arithmetic" optimization can be
# used if you are only targeting Android 2.0 or later.)  Make sure you
# test thoroughly if you go this route.
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-dontwarn
# The remainder of this file is identical to the non-optimized version
# of the Proguard configuration file (except that the other file has
# flags to turn off optimization).
-ignorewarnings
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**


# 可注解配置 @keep
# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}


###################################################
#反射和泛型
###################################################
-keepattributes SourceFile,LineNumberTable,Signature
-keepattributes EnclosingMethod


###################################################
#bean
###################################################
-keep class com.yeepay.mops.manager.model.**
-keepclassmembers  class com.yeepay.mops.manager.model.**{
    *;
}
-keep class com.yeepay.mops.manager.request.**
-keepclassmembers  class com.yeepay.mops.manager.request.**{
    *;
}
-keep class com.yeepay.mops.manager.response.**
-keepclassmembers  class com.yeepay.mops.manager.response.**{
    *;
}



###################################################
#不混淆Serializable接口的子类中指定的某些成员变量和方法
###################################################
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


###################################################
#移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用，
#另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
###################################################
#-assumenosideeffects class android.util.Log {
#    public static *** v(...);
#    public static *** i(...);
#    public static *** d(...);
#    public static *** w(...);
#    public static *** e(...);
#}


###################################################
#webview js
###################################################

-keep public class com.yeepay.mops.manager.javascript.JavaScriptInterface{*;}

###################################################
#第三方包
###################################################
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

#google 系列
-dontwarn com.google.**
-keep  class com.google.**{*;}
-keep  interface com.google.**{*;}

-dontwarn com.baidu.**
-keep class com.baidu.**{*; }

-dontwarn cn.yolanda.nohttp.**
-keep class cn.yolanda.nohttp.** { *;}
-keep interface cn.yolanda.nohttp.** { *;}


-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes SourceFile,LineNumberTable


-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
