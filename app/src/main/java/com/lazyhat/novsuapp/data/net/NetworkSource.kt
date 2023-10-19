package com.lazyhat.novsuapp.data.net

import com.lazyhat.novsuapp.data.base.BaseIO
import com.lazyhat.novsuapp.data.model.Group
import com.lazyhat.novsuapp.data.model.Lesson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher

private val authority = "http://lazyhat.ru"

interface NetworkSource : BaseIO {
    suspend fun getGroups(): List<Group>
    suspend fun getLessons(groupId: UInt): List<Lesson>
}

class NetworkSourceImpl(
    private val client: HttpClient,
    override val IODispatcher: CoroutineDispatcher
) : NetworkSource {
    override suspend fun getGroups(): List<Group> = withIOContext {
        client.get("$authority/groups").body()
    }

    override suspend fun getLessons(groupId: UInt): List<Lesson> = withIOContext {
        client.get("$authority/groups/$groupId").body()
    }
}