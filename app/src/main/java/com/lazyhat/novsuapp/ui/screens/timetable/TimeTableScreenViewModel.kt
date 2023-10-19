package com.lazyhat.novsuapp.ui.screens.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lazyhat.novsuapp.data.model.AsyncLessons
import com.lazyhat.novsuapp.data.model.AsyncWeekLessons
import com.lazyhat.novsuapp.data.model.Week
import com.lazyhat.novsuapp.data.repo.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class TimeTableScreenViewModel(private val mainRepository: MainRepository) :
    ViewModel() {
    private val _lessons = mainRepository.lessons
    private val _enteredWeek = MutableStateFlow(Week.All)
    val uiState: StateFlow<TimeTableScreenState> =
        combine(_lessons, _enteredWeek) { lessons, week ->
            TimeTableScreenState(
                week,
                when (lessons) {
                    AsyncLessons.Error -> AsyncWeekLessons.Error
                    AsyncLessons.Loading -> AsyncWeekLessons.Loading
                    AsyncLessons.NoGroupSelected -> AsyncWeekLessons.NoGroupSelected
                    is AsyncLessons.Success -> AsyncWeekLessons.Success(lessons.weekLessons[week.ordinal].dowLessons)
                }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TimeTableScreenState(Week.All, AsyncWeekLessons.Loading)
        )

    fun nextWeek() {
        _enteredWeek.update {
            when (it) {
                Week.Upper -> Week.Lower
                Week.Lower -> Week.All
                Week.All -> Week.Upper
            }
        }
    }
}