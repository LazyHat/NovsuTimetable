package com.lazyhat.novsuapp.data.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

interface BaseIO {
    val IODispatcher: CoroutineDispatcher
    suspend fun <T> withIOContext(body: suspend CoroutineScope.() -> T): T =
        withContext(IODispatcher, body)
}