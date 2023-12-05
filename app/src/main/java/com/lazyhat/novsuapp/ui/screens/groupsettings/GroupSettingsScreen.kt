package com.lazyhat.novsuapp.ui.screens.groupsettings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lazyhat.novsuapp.R
import com.lazyhat.novsuapp.data.model.Grade
import com.lazyhat.novsuapp.data.model.Group
import com.lazyhat.novsuapp.data.model.GroupParameters
import com.lazyhat.novsuapp.data.model.Institute
import com.lazyhat.novsuapp.data.repo.MainRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSettingsScreen(openDrawer: () -> Unit) {
    val vm = koinViewModel<GroupSettingsScreenViewModel>()
    val uiState by vm.uiState.collectAsState()
    Scaffold(topBar = {
        TopAppBar(navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    Icons.Default.Home, null,
                    Modifier
                        .scale(1.5f)
                        .padding(5.dp)
                )
            }
        }, title = {
            Text(stringResource(id = R.string.group_settings))
        })
    }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var expandedIndex by remember { mutableStateOf<Int?>(null) }
            DropDownOutlinedMenu(state = uiState.grade,
                values = Grade.entries,
                onChangeValue = { vm.createEvent(GroupSettingsScreenEvent.ChangeGrade(it)) },
                expanded = expandedIndex == 0,
                onClose = { expandedIndex = null },
                onExpand = { expandedIndex = 0 },
                label = stringResource(id = R.string.grade),
                modifier = Modifier.fillMaxWidth(),
                printValue = { it.number.toString() })
            DropDownOutlinedMenu(
                state = uiState.institute,
                values = Institute.entries,
                onChangeValue = { vm.createEvent(GroupSettingsScreenEvent.ChangeInstitute(it)) },
                expanded = expandedIndex == 1,
                onClose = { expandedIndex = null },
                onExpand = { expandedIndex = 1 },
                label = stringResource(id = R.string.institute),
                modifier = Modifier.fillMaxWidth(),
                printValue = { stringResource(id = it.label)}
            )
            DropDownOutlinedMenu(
                state = uiState.selected,
                expanded = expandedIndex == 2,
                onClose = { expandedIndex = null },
                onExpand = { expandedIndex = 2 },
                values = uiState.availableGroups,
                onChangeValue = { vm.createEvent(GroupSettingsScreenEvent.EnterGroup(it)) },
                label = stringResource(id = R.string.group),
                modifier = Modifier.fillMaxWidth(),
                printValue = { it.name }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropDownOutlinedMenu(
    state: T?,
    expanded: Boolean,
    onClose: () -> Unit,
    onExpand: () -> Unit,
    values: List<T>,
    modifier: Modifier = Modifier,
    label: String? = null,
    ifNullValue: String = stringResource(id = R.string.not_setted),
    printValue: @Composable (T) -> String,
    onChangeValue: (T) -> Unit
) {
    val animateTrailingIconRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "trail Animation",
        animationSpec = tween(200, 0, LinearEasing)
    )
    val animateTrailingIconColor by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSecondaryContainer,
        label = "",
        animationSpec = tween(200, 0, LinearEasing)
    )
    ExposedDropdownMenuBox(expanded = expanded,
        onExpandedChange = { if (it) onExpand() else onClose() }) {
        OutlinedTextField(value = state?.let { printValue(it) } ?: ifNullValue,
            onValueChange = {},
            readOnly = true,
            modifier = modifier.menuAnchor(),
            label = label?.let { { Text(it) } },
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    null,
                    modifier = Modifier.rotate(animateTrailingIconRotation),
                    tint = animateTrailingIconColor
                )
            })
        DropdownMenu(expanded = expanded, onDismissRequest = onClose) {
            values.forEach {
                DropdownMenuItem(text = {
                    Text(printValue(it), fontSize = 20.sp)
                },
                    onClick = { onChangeValue(it);onClose() })
            }
        }
    }
}


class GroupSettingsScreenViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _groups = MutableStateFlow(listOf<Group>())
    private val _institute = MutableStateFlow<Institute?>(null)
    private val _grade = MutableStateFlow<Grade?>(null)
    private val _selected = MutableStateFlow<Group?>(null)
    private val _subGroup = MutableStateFlow<UByte?>(0U)

    val uiState = combine(
        _institute, _grade, _selected, _subGroup, _groups
    ) { inst, grade, selected, subgroup, groups ->
        GroupSettingsScreenState(
            inst,
            grade,
            selected,
            subgroup,
            groups.filter { (inst == null || it.institute == inst) && (grade == null || it.grade == grade) })
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), GroupSettingsScreenState.Default
    )

    init {
        viewModelScope.launch {
            updateGroupSettings()
            _groups.update { mainRepository.getAllGroups() }
        }
    }

    fun createEvent(e: GroupSettingsScreenEvent) = onEvent(e)

    private fun onEvent(e: GroupSettingsScreenEvent): Any = when (e) {
        is GroupSettingsScreenEvent.ChangeGrade -> _grade.update { e.new }
        is GroupSettingsScreenEvent.ChangeInstitute -> _institute.update { e.new }
        is GroupSettingsScreenEvent.EnterGroup ->
            viewModelScope.launch {
                mainRepository.putGroupParameters(
                    GroupParameters(
                        e.group, 0U
                    )
                )
                updateGroupSettings()
            }
    }

    private suspend fun updateGroupSettings() {
        mainRepository.parameters.first().let {
            _institute.value = it?.group?.institute
            _grade.value = it?.group?.grade
            _selected.value = it?.group
            _subGroup.value = it?.subGroup
        }
    }
}

data class GroupSettingsScreenState(
    val institute: Institute?,
    val grade: Grade?,
    val selected: Group?,
    val subGroup: UByte?,
    val availableGroups: List<Group>
) {
    companion object {
        val Default = GroupSettingsScreenState(
            null, null, null, null, listOf()
        )
    }
}

sealed class GroupSettingsScreenEvent {
    data class ChangeGrade(val new: Grade) : GroupSettingsScreenEvent()
    data class ChangeInstitute(val new: Institute) : GroupSettingsScreenEvent()
    data class EnterGroup(val group: Group) : GroupSettingsScreenEvent()
    // data class ChangeSubgroup(val new: UByte) : GroupSettingsScreenEvent()
}