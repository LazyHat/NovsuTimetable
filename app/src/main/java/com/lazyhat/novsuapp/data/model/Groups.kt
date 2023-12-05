package com.lazyhat.novsuapp.data.model

import androidx.annotation.StringRes
import com.lazyhat.novsuapp.R
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
enum class Institute(
	@StringRes
	val label: Int
) {
	IEIS(R.string.institute_ieis),
	ICEUS(R.string.institute_iceus),
	INPO(R.string.institute_inpo),
	IBHI(R.string.institute_ibhi),
	IGUM(R.string.institute_igum),
	IMO(R.string.institute_imo),
	IUR(R.string.institute_iur),
	IPT(R.string.institute_ipt)
}

@Serializable
@Suppress("Unused")
enum class GroupQualifier {
	DO,
	DU,
	VO,
	VU,
	ZO,
	ZU
}

@Serializable
enum class Grade(val number: Int) {
	First(1),
	Second(2),
	Third(3),
	Fourth(4),
	Fifth(5),
	Sixth(6)
}

@Serializable
data class Group(
	val id: UInt,
	val name: String,
	val institute: Institute,
	val grade: Grade,
	val qualifier: GroupQualifier,
	val entryYear: Short,
	val lastUpdated: LocalDateTime
) {
	//CHANGE AFTER CHANGING CLASS FIELDS;
	//LAST UPDATED DELETED ON PURPOSE //LAST UPDATED УДАЛЕНО НАМЕРЕННО!!!
	fun equalWithoutLastUpdated(other: Group): Boolean {
		if (id != other.id) return false
		if (name != other.name) return false
		if (institute != other.institute) return false
		if (qualifier != other.qualifier) return false
		return entryYear == other.entryYear
	}

	fun equalWithoutLUandID(other: Group): Boolean{
		if (name != other.name) return false
		if (institute != other.institute) return false
		if (qualifier != other.qualifier) return false
		return entryYear == other.entryYear
	}
}

@Serializable
data class GroupParameters(
	val group: Group, val subGroup: UByte?
)