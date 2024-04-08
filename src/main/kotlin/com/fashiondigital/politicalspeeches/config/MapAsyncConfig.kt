package com.fashiondigital.politicalspeeches.config

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withTimeout

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
