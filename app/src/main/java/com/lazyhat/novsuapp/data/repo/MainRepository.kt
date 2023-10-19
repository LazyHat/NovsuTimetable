package com.lazyhat.novsuapp.data.repo

import com.lazyhat.novsuapp.data.local.GroupParametersLocalSource
import com.lazyhat.novsuapp.data.model.AsyncLessons
import com.lazyhat.novsuapp.data.model.Group
import com.lazyhat.novsuapp.data.model.GroupParameters
import com.lazyhat.novsuapp.data.model.toGroupedLessons
import com.lazyhat.novsuapp.data.net.NetworkSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


interface MainRepository {
    val lessons: StateFlow<AsyncLessons>
    fun refreshLessons()
    suspend fun getAllGroups(): List<Group>
    suspend fun putGroupParameters(parameters: GroupParameters)
    suspend fun getGroupParameters(): GroupParameters?
}

class MainRepositoryImpl(
    private val groupParametersLocalSource: GroupParametersLocalSource,
    private val networkSource: NetworkSource
) : MainRepository {
    private val _lessons = MutableStateFlow<AsyncLessons>(AsyncLessons.Loading)
    override val lessons = _lessons.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            groupParametersLocalSource.getParametersFlow().collectLatest { it ->
                _lessons.update { AsyncLessons.Loading }
                _lessons.update { _ ->
                    it?.let { parameters ->
                        try {
                            networkSource.getLessons(parameters.group.id)
                                .toGroupedLessons().weekLessons.let { AsyncLessons.Success(it) }
                        } catch (e: Exception) {
                            AsyncLessons.Error
                        }
                    } ?: AsyncLessons.NoGroupSelected
                }
            }
        }
    }

    override fun refreshLessons() {
        networkSource
    }

    override suspend fun getAllGroups(): List<Group> = try {
        networkSource.getGroups()
    } catch (e: Exception) {
        listOf()
    }

    override suspend fun putGroupParameters(parameters: GroupParameters) {
        groupParametersLocalSource.putParameters(parameters)
    }

    override suspend fun getGroupParameters(): GroupParameters? =
        groupParametersLocalSource.getParametersFlow().first()
}