package com.lazyhat.novsuapp.data.model

import kotlinx.datetime.DayOfWeek

fun List<Lesson>.toGroupedLessons(): Lessons {
    val k = Lessons(Week.entries.map { week ->
        WeekLessons(week, DayOfWeek.values().dropLast(1).map { dow ->
            DOWLessons(
                dow,
                this.filter {
                    (week == Week.All || it.week == Week.All || it.week == week) &&
                            it.dow == dow
                })
        })
    })
    return k
}