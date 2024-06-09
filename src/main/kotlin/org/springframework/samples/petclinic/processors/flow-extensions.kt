package org.springframework.samples.petclinic.processors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull


public inline fun <T, R : Any> Flow<T>.mapNotNull(
    name: String,
    crossinline transform: suspend (value: T) -> R?
): Flow<R> {
    println("in my mapNotNull: name = $name,  t=${Thread.currentThread().name}")

    return mapNotNull(transform)
}


public inline fun <T> Flow<T>.filter(name: String, crossinline predicate: suspend (T) -> Boolean): Flow<T> {
    println("in my filter: name = $name,  t=${Thread.currentThread().name}")
    return filter(predicate)
}

