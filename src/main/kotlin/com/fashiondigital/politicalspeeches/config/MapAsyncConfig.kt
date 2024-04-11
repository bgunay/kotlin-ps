package com.fashiondigital.politicalspeeches.config

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withTimeout

/**
 * Maps the given [Iterable] of type [T] to a new [List] of type [R] using the provided [transformation] function.
 * The mapping is performed asynchronously, allowing for concurrent execution of the transformation function.
 * The operation will timeout after the specified [timeout] duration.
 *
 * @param transformation A suspend function that takes an element of type [T] and returns an element of type [R].
 * @param timeout The maximum duration for the operation to complete before it times out.
 * @return A new [List] of type [R] containing the results of the transformation function applied to each element of the input [Iterable].
 */
suspend fun <T, R> Iterable<T>.mapAsync(transformation: suspend (T) -> R, timeout: Long)
        : List<R> = withTimeout(timeout) {
    this@mapAsync
        .map { async { transformation(it) } }
        .awaitAll()
}

//suspend fun <T, R> Iterable<T>.mapAsync(
//    concurrency: Int,
//    transformation: suspend (T) -> R,
//): List<R> = coroutineScope {
//    val semaphore = Semaphore(concurrency)
//    this@mapAsync
//        .map { async { semaphore.withPermit { transformation(it) } } }
//        .awaitAll()
//}
