package com.lazyhat.novsuapp.data.net

import com.lazyhat.novsuapp.data.base.BaseIO
import com.lazyhat.novsuapp.data.model.Group
import com.lazyhat.novsuapp.data.model.Lesson
import com.lazyhat.novsuapp.data.model.Week
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

private val authority: URLBuilder
	get() = URLBuilder(URLProtocol.HTTP, "lazyhat.ru", pathSegments = listOf("tt")).clone()

interface NetworkSource : BaseIO {
	suspend fun getGroups(): List<Group>?
	suspend fun getLessons(groupId: UInt): List<Lesson>?
	suspend fun getGroupInfo(groupId: UInt): Group?
	suspend fun getWeek(): Week?
}

class NetworkSourceImpl(
	private val client: HttpClient, override val IODispatcher: CoroutineDispatcher
) : NetworkSource {
	override suspend fun getGroups(): List<Group>? = withIOContext {
		client.get(authority.appendPathSegments("groups").build()).takeIf { it.status == HttpStatusCode.OK }?.body()
	}

	override suspend fun getLessons(groupId: UInt): List<Lesson>? = withIOContext {
		client.get(authority.appendPathSegments("groups", groupId.toString(), "lessons").build())
			.takeIf { it.status == HttpStatusCode.OK }
			?.body()
	}

	override suspend fun getGroupInfo(groupId: UInt): Group? = withIOContext {
		client.get(authority.appendPathSegments("groups", groupId.toString()).build()).takeIf {
			it.status == HttpStatusCode.OK
		}?.body()
	}

	override suspend fun getWeek(): Week? = withIOContext {
		client.get(authority.appendPathSegments("week").build()).takeIf { it.status == HttpStatusCode.OK }?.body()
	}
}