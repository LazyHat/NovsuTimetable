package com.lazyhat.novsuapp.ui.screens.timetable

import com.lazyhat.novsuapp.data.model.AsyncWeekLessons
import com.lazyhat.novsuapp.data.model.Week

data class TimeTableScreenState(
    val selectedWeek: Week,
    val lessons: AsyncWeekLessons
)