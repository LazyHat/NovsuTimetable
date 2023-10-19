package com.lazyhat.novsuapp.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class Institute { IEIS, ICEUS, INPO, IBHI, IGUM, IMO, IUR, IPT }

@Serializable
enum class GroupQualifier { DO, DU, VO, VU, ZO, ZU }

@Serializable
enum class Grade(val number: Int) { First(1), Second(2), Third(3), Fourth(4), Fifth(5), Sixth(6) }

@Serializable
data class Group(
    val id: UInt,
    val name: String,
    val institute: Institute,
    val grade: Grade,
    val qualifier: GroupQualifier,
    val entryYear: Short
)

@Serializable
data class GroupParameters(
    val group: Group,
    val subGroup: UByte?
)