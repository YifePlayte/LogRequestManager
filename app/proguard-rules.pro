-keep class com.yifeplayte.logrequestmanager.hook.MainHook { <init>(); }-keep class * extends com.yifeplayte.logrequestmanager.hook.hooks.* {    <init>();    com.yifeplayte.logrequestmanager.hook.hooks.** INSTANCE;}-keepattributes RuntimeVisibleAnnotations