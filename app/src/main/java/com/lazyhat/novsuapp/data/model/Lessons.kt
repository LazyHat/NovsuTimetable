package com.lazyhat.novsuapp.data.model

import androidx.annotation.StringRes
import com.lazyhat.novsuapp.R
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
enum class Week(@StringRes val label: Int) {
    Upper(R.string.week_upper),
    Lower(R.string.week_lower),
    All(R.string.week_all)
}

@Serializable
enum class LessonType(@StringRes val normal: Int, @StringRes val short: Int) {
    @Suppress("unused")
    Consultation(
        R.string.type_consultation_normal,
        R.string.type_consultation_short
    ),
    Lecture(
        R.string.type_lecture_normal,
        R.string.type_lecture_short
    ),
    Practice(
        R.string.type_practice_normal,
        R.string.type_practice_short
    ),
    Lab(
        R.string.type_laboratory_normal,
        R.string.type_laboratory_short
    )
}

@Serializable
data class Lesson(
    val id: UInt,
    val title: String,
    val dow: DayOfWeek,
    val week: Week,
    val group: UInt,
    val subgroup: UByte,
    val teacher: String,
    val auditorium: String,
    val type: List<LessonType>,
    val startHour: UByte,
    val durationInHours: UByte,
    val description: String
)


sealed class AsyncLessons {
    data class Success(val weekLessons: List<WeekLessons>) : AsyncLessons()
    data object Loading : AsyncLessons()
    data object NoGroupSelected : AsyncLessons()
    data object Error : AsyncLessons()
}

sealed class AsyncWeekLessons {
    data class Success(val dowLessons: List<DOWLessons>) : AsyncWeekLessons()
    data object Loading : AsyncWeekLessons()
    data object NoGroupSelected : AsyncWeekLessons()
    data object Error : AsyncWeekLessons()
}

data class Lessons(
    val weekLessons: List<WeekLessons>
)

data class WeekLessons(
    val week: Week,
    val dowLessons: List<DOWLessons>,
)

data class DOWLessons(
    val dow: DayOfWeek,
    val lessons: List<Lesson>
)


