package com.lazyhat.novsuapp.ui.screens.timetable

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lazyhat.novsuapp.R
import com.lazyhat.novsuapp.data.model.AsyncWeekLessons
import com.lazyhat.novsuapp.data.model.Lesson
import com.lazyhat.novsuapp.data.model.LessonType
import com.lazyhat.novsuapp.data.model.Week
import com.lazyhat.novsuapp.ui.screens.other.LoadingPage
import com.lazyhat.novsuapp.ui.theme.NovsuTimeTableTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.koin.androidx.compose.koinViewModel
import java.time.format.TextStyle
import java.util.Locale

private fun LocalDateTime.Companion.now(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(openDrawer: () -> Unit) {
	val vm = koinViewModel<TimetableScreenViewModel>()
	val uiState by vm.uiState.collectAsState()
	val scope = rememberCoroutineScope()
	val pagerState = rememberPagerState(LocalDateTime.now().dayOfWeek.let {
		if (it == java.time.DayOfWeek.SUNDAY) DayOfWeek.SATURDAY else it
	}.ordinal) {
		java.time.DayOfWeek.entries.size - 1
	}
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
			Column {
				Text(stringResource(id = R.string.app_name))
				AnimatedVisibility(visible = uiState.week != Week.All) {
					Text("${stringResource(id = R.string.week)}: ${stringResource(id = uiState.week.label)}", fontSize = 15.sp)
				}
			}
		}, actions = {
			uiState.groupParameters?.let {
				Text("${stringResource(id = R.string.last_updated)}\n${
					it.group.lastUpdated.let {
						"${it.date.dayOfMonth}.${it.date.monthNumber}.${it.year} ${it.time.hour}:${it.time.minute}"
					}
				}", modifier = Modifier
					.padding(3.dp)
					.clickable { vm.updateLessons() })
			}
			Spacer(Modifier.width(10.dp))
		})
	}, bottomBar = {
		if (uiState.lessons is AsyncWeekLessons.Success) TabRow(selectedTabIndex = pagerState.currentPage) {
			0.until(pagerState.pageCount).map { java.time.DayOfWeek.entries[it] }.forEachIndexed { index, dow ->
				Tab(selected = pagerState.currentPage == index, onClick = {
					scope.launch {
						pagerState.animateScrollToPage(index)
					}
				}) {
					Text(
						dow.getDisplayName(
							TextStyle.SHORT, Locale("ru", "ru")
						).let {
							it[0].uppercase() + it.drop(1)
						}, fontSize = 20.sp, fontWeight = Bold, modifier = Modifier.padding(vertical = 10.dp)
					)
				}
			}
		}
	}, floatingActionButton = {
		if (uiState.lessons is AsyncWeekLessons.Success) FloatingActionButton(onClick = { vm.nextWeek() }) {
			AnimatedContent(targetState = uiState.selectedWeek, label = "animatedFAB",
							transitionSpec = {
								slideIn(initialOffset = { IntOffset.Zero }) togetherWith slideOut(targetOffset = { IntOffset.Zero })
							}) {
				Text(stringResource(it.label), textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
			}
		}
	}) { padding ->
		Box(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
		) {
			uiState.let { state ->
				when (state.lessons) {
					AsyncWeekLessons.Loading -> {
						LoadingPage(modifier = Modifier.fillMaxSize())
					}

					AsyncWeekLessons.Error -> {
						Text(
							stringResource(id = R.string.error_unknown), Modifier.align(Alignment.Center)
						)
					}

					AsyncWeekLessons.NoGroupSelected -> {
						Text(
							stringResource(id = R.string.error_no_group_selected), Modifier.align(Alignment.Center)
						)
					}

					is AsyncWeekLessons.Success -> {
						Crossfade(
							targetState = state.lessons, label = "crossfadeChangeWeek"
						) { crossfadeState ->
							HorizontalPager(state = pagerState) { page ->
								LazyColumn(
									modifier = Modifier
										.fillMaxSize()
										.padding(horizontal = 10.dp),
									verticalArrangement = Arrangement.spacedBy(10.dp)
								) {
									items(crossfadeState.dowLessons[page].lessons) {
										TimetableScreenLessonCard(
											data = it, state.selectedWeek != Week.All
										)
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

@Composable
private fun TimetableScreenLessonCard(data: Lesson, weekNotAll: Boolean) {
	Card(
		shape = RoundedCornerShape(5.dp)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(15.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
			) {
				Text(
					text = data.title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(2f)
				)
				Text(text = data.type.let {
					if (it.size == 1) listOf(it.first().normal) else data.type.map { it.short }
				}.map { stringResource(id = it) }.joinToString("/"),
					 fontSize = 13.sp,
					 modifier = Modifier.weight(1f),
					 textAlign = TextAlign.End
				)
			}
			Spacer(Modifier.height(10.dp))
			Card(
				shape = RoundedCornerShape(5.dp)
			) {
				Text(
					text = "${data.startHour}:00-${data.startHour + data.durationInHours - 1U}:45",
					fontSize = 18.sp,
					fontWeight = Bold
				)
			}
			var spacer by remember { mutableStateOf(false) }
			var count = 0
			if (spacer) {
				Spacer(Modifier.height(10.dp))
			}
			if (!weekNotAll && data.week != Week.All) {
				Text("${stringResource(id = R.string.week)}: ${stringResource(data.week.label)}")
				count++
			}
			if (data.subgroup != 0u.toUByte()) {
				Text("${stringResource(id = R.string.subgroup)}: ${data.subgroup}")
				count++
			}
			if (data.auditorium.isNotEmpty()) {
				Text("${stringResource(id = R.string.audithorium)}: ${data.auditorium}")
				count++
			}
			if (data.teacher.isNotEmpty()) {
				Text(data.teacher)
				count++
			}
			if (data.description.isNotEmpty()) {
				Text(data.description)
				count++
			}
			spacer = count != 0
		}
	}
}

@Preview
@Composable
fun PreviewTimetableLessonCard() {
	NovsuTimeTableTheme(darkTheme = true) {
		Surface {
			Box(modifier = Modifier.fillMaxWidth()) {
				TimetableScreenLessonCard(
					Lesson(
						1U,
						"Lesson Title",
						DayOfWeek.FRIDAY,
						Week.Upper,
						1U,
						1U,
						teacher = "Lesson Teacher",
						"Lesson Auditorium",
						listOf(LessonType.Lab, LessonType.Lecture, LessonType.Practice),
						14U,
						2U,
						"Lesson Description"
					), true
				)
			}
		}
	}
}