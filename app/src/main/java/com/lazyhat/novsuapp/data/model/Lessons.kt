package com.lazyhat.novsuapp.data.model

import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable

@Serializable
enum class Week { Upper, Lower, All }

@Serializable
enum class LessonType(val short: String) {
    Consultation("Cons"), Lecture("Lec"), Practice("Prac"), Lab("Lab")
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


