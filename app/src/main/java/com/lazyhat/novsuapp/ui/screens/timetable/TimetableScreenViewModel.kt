package com.lazyhat.novsuapp.ui.screens.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lazyhat.novsuapp.data.model.AsyncLessons
import com.lazyhat.novsuapp.data.model.AsyncWeekLessons
import com.lazyhat.novsuapp.data.model.Week
import com.lazyhat.novsuapp.data.repo.MainRepository
import kotlinx.coroutines.flow.*

class TimetableScreenViewModel(private val mainRepository: MainRepository) : ViewModel() {
	private val _groupParameters = mainRepository.parameters
	private val _lessons = mainRepository.lessons
	private val _week = mainRepository.week
	private val _enteredWeek = MutableStateFlow(Week.All)
	val uiState: StateFlow<TimetableScreenState> =
		combine(_lessons, _enteredWeek, _groupParameters, _week) { lessons, enteredWeek, groupParameters, week ->
			TimetableScreenState(
				groupParameters, enteredWeek, when (lessons) {
					AsyncLessons.Error           -> AsyncWeekLessons.Error
					AsyncLessons.Loading         -> AsyncWeekLessons.Loading
					AsyncLessons.NoGroupSelected -> AsyncWeekLessons.NoGroupSelected
					is AsyncLessons.Success      -> AsyncWeekLessons.Success(lessons.weekLessons[week.ordinal].dowLessons)
				},
				week
			)
		}.stateIn(
			viewModelScope, SharingStarted.WhileSubscribed(5000), TimetableScreenState(
				null, Week.All, AsyncWeekLessons
					.Loading, Week.All
			)
		)

	fun nextWeek() {
		_enteredWeek.update {
			when (it) {
				Week.Upper -> Week.Lower
				Week.Lower -> Week.All
				Week.All   -> Week.Upper
			}
		}
	}

	fun updateLessons() {
		mainRepository.refreshLessons()
	}
}