package aok

import kotlin.reflect.KClass

val <T : Any> KClass<out T>.sealedObjects: Iterable<T>
    get() = (objectInstance?.let { listOf(it) } ?: emptyList()) + sealedSubclasses
        .flatMap(KClass<out T>::sealedObjects)
