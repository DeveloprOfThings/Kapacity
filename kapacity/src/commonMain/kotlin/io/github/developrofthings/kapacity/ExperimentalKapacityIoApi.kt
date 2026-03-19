package io.github.developrofthings.kapacity

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING, // Or ERROR to be stricter
    message = "This Kapacity API is experimental and subject to change."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class ExperimentalKapacityApi