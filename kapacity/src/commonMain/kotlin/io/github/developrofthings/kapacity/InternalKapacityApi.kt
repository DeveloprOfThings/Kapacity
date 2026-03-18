package io.github.developrofthings.kapacity

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is an internal Kapacity API. It can change or be removed at any time. Do not use it in your application code."
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION
)
annotation class InternalKapacityApi