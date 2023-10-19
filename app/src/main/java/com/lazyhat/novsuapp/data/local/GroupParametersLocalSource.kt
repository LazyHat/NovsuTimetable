package com.lazyhat.novsuapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.lazyhat.novsuapp.data.model.GroupParameters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

interface GroupParametersLocalSource : com.lazyhat.novsuapp.data.base.BaseIO {
    fun getParametersFlow(): Flow<GroupParameters?>
    suspend fun putParameters(parameters: GroupParameters)
    suspend fun resetParameters()
}

class GroupParametersLocalSourceImpl(
    private val dataStore: DataStore<GroupParameters?>,
    override val IODispatcher: CoroutineDispatcher
) :
    GroupParametersLocalSource {
    override fun getParametersFlow(): Flow<GroupParameters?> = dataStore.data

    override suspend fun putParameters(parameters: GroupParameters): Unit = withIOContext {
        dataStore.updateData { parameters }
    }

    override suspend fun resetParameters(): Unit = withIOContext {
        dataStore.updateData { null }
    }
}

object GroupParametersSerializer : Serializer<GroupParameters?> {
    override val defaultValue: GroupParameters?
        get() = null

    override suspend fun readFrom(input: InputStream): GroupParameters? =
        input.readBytes().decodeToString().let { Json.decodeFromString(it) }

    override suspend fun writeTo(t: GroupParameters?, output: OutputStream) =
        withContext(Dispatchers.IO) {
            Json.encodeToString(t).encodeToByteArray().let { output.write(it) }
        }
}