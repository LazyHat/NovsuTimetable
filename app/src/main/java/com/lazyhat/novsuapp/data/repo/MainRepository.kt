package com.lazyhat.novsuapp.data.repo

import com.lazyhat.novsuapp.data.local.GroupParametersLocalSource
import com.lazyhat.novsuapp.data.model.*
import com.lazyhat.novsuapp.data.net.NetworkSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


interface MainRepository {
	val lessons: Flow<AsyncLessons>
	val parameters: Flow<GroupParameters?>
	val week: Flow<Week>
	fun refreshLessons()
	suspend fun getAllGroups(): List<Group>
	suspend fun putGroupParameters(groupParameters: GroupParameters)
}

class MainRepositoryImpl(
	private val groupParametersLocalSource: GroupParametersLocalSource, private val networkSource: NetworkSource
) : MainRepository {
	private val scope = CoroutineScope(Dispatchers.IO)
	private val _lessons = MutableStateFlow<AsyncLessons>(AsyncLessons.Loading)
	private val _week = MutableStateFlow(Week.All)

	override val lessons: Flow<AsyncLessons>
		get() = _lessons
	override val parameters: Flow<GroupParameters?>
		get() = groupParametersLocalSource.parameters

	override val week: Flow<Week>
		get() = _week

	init {
		CoroutineScope(Dispatchers.IO).also {
			it.launch {
				groupParametersLocalSource.parameters.collectLatest {
					updateLessons(it)
				}
			}
			it.launch {
				_week.update {
					networkSource.getWeek()
						?: Week.All
				}
			}
		}
	}

	override fun refreshLessons() {
		_lessons.value = AsyncLessons.Loading
		scope.launch {
			parameters.first()?.let all@{
				val newGroup = networkSource.getGroupInfo(it.group.id)
					?: run {
						groupParametersLocalSource.parameters.first()?.let { params ->
							putGroupParameters(params.copy(group = getAllGroups().find { it.equalWithoutLUandID(params.group) }
								?: run {
									_lessons.value = AsyncLessons.NoGroupSelected
									return@all
								}))
						}
						return@all
					}
				if (it.group.equalWithoutLastUpdated(newGroup)) {
					groupParametersLocalSource.putGroupParameters(it.copy(newGroup))
				} else {
					_lessons.value = AsyncLessons.NoGroupSelected
				}
			}
				?: run { _lessons.value = AsyncLessons.NoGroupSelected }
		}
	}

	override suspend fun getAllGroups(): List<Group> = networkSource.getGroups()
		?: listOf()

	override suspend fun putGroupParameters(groupParameters: GroupParameters) {
		groupParametersLocalSource.putGroupParameters(groupParameters)
	}

	suspend fun updateLessons(group: GroupParameters?) {
		_lessons.value = AsyncLessons.Loading
		_lessons.value = group?.let { parameters ->
			try {
				networkSource.getLessons(parameters.group.id)?.let {
					it.toGroupedLessons().weekLessons.let {
						AsyncLessons.Success(it)
					}
				}
					?: AsyncLessons.Error
			} catch (e: Exception) {
				AsyncLessons.Error
			}
		}
			?: AsyncLessons.NoGroupSelected
	}
}