# kotlinx.serialization DTOs
-keepattributes *Annotation*, InnerClasses
-keep,includedescriptorclasses class com.shortdrama.ai.core.data.model.**$$serializer { *; }
-keepclassmembers class com.shortdrama.ai.core.data.model.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
