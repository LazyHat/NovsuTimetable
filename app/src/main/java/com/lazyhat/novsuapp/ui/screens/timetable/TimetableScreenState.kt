package com.lazyhat.novsuapp.ui.screens.timetable

import com.lazyhat.novsuapp.data.model.AsyncWeekLessons
import com.lazyhat.novsuapp.data.model.GroupParameters
import com.lazyhat.novsuapp.data.model.Week

data class TimetableScreenState(
    val groupParameters: GroupParameters?,
    val selectedWeek: Week,
    val lessons: AsyncWeekLessons,
    val week: Week
)