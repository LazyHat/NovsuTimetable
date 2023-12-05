package com.lazyhat.novsuapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.lazyhat.novsuapp.data.base.BaseIO
import com.lazyhat.novsuapp.data.model.GroupParameters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

interface GroupParametersLocalSource : BaseIO {
	val parameters: Flow<GroupParameters?>
	suspend fun putGroupParameters(new: GroupParameters)
	suspend fun reset()
}

class GroupParametersLocalSourceImpl(
	private val dataStore: DataStore<GroupParameters?>, override val IODispatcher: CoroutineDispatcher
) : GroupParametersLocalSource {
	override val parameters: Flow<GroupParameters?>
		get() = dataStore.data

	override suspend fun putGroupParameters(new: GroupParameters): Unit = withIOContext {
		dataStore.updateData { new }
	}

	override suspend fun reset(): Unit = withIOContext {
		dataStore.updateData { null }
	}
}

object GroupParametersSerializer : Serializer<GroupParameters?> {
	override val defaultValue: GroupParameters?
		get() = null

	override suspend fun readFrom(input: InputStream): GroupParameters? =
		input.readBytes().decodeToString().let { Json.decodeFromString(it) }

	override suspend fun writeTo(t: GroupParameters?, output: OutputStream) = withContext(Dispatchers.IO) {
		Json.encodeToString(t).encodeToByteArray().let { output.write(it) }
	}
}