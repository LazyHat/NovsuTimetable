package com.lazyhat.novsuapp.ui.screens.timetable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import kotlinx.datetime.DayOfWeek
import org.koin.androidx.compose.koinViewModel
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimeTableScreen(openDrawer: () -> Unit) {
    val vm = koinViewModel<TimeTableScreenViewModel>()
    val uiState by vm.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(0) { DayOfWeek.values().size - 1 }
    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = openDrawer) {
                    Icon(
                        Icons.Default.Home, null,
                        Modifier
                            .scale(1.5f)
                            .padding(5.dp)
                    )
                }
            }, title = { Text(stringResource(id = R.string.app_name)) })
        },
        bottomBar = {
            if (uiState.lessons is AsyncWeekLessons.Success)
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    0.until(pagerState.pageCount)
                        .map { DayOfWeek.values()[it] }
                        .forEachIndexed { index, dow ->
                            Tab(selected = pagerState.currentPage == index, onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }) {
                                Text(
                                    dow.getDisplayName(
                                        TextStyle.SHORT,
                                        Locale("ru", "ru")
                                    ).let {
                                        it[0].uppercase() + it.drop(1)
                                    },
                                    fontSize = 20.sp,
                                    fontWeight = Bold,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                }
        },
        floatingActionButton = {
            if (uiState.lessons is AsyncWeekLessons.Success)
                FloatingActionButton(onClick = { vm.nextWeek() }) {
                    Text(uiState.selectedWeek.name)
                }
        }
    ) { padding ->
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
                            "Error",
                            Modifier.align(Alignment.Center)
                        )
                    }

                    AsyncWeekLessons.NoGroupSelected -> {
                        Text(
                            "No group selected",
                            Modifier.align(Alignment.Center)
                        )
                    }

                    is AsyncWeekLessons.Success -> {
                        Crossfade(
                            targetState = state.lessons,
                            label = "crossfadeChangeWeek"
                        ) { crossfadeState ->
                            HorizontalPager(state = pagerState) { page ->
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(crossfadeState.dowLessons[page].lessons) {
                                        TimeTableScreenLessonCard(
                                            data = it,
                                            state.selectedWeek != Week.All
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
private fun TimeTableScreenLessonCard(data: Lesson, weekNotAll: Boolean) {
    Card(
        shape = RoundedCornerShape(5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = data.type.let {
                        if (it.size == 1) it.first().name else data.type.joinToString(
                            "/"
                        ) { it.short }
                    },
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
                Text("week: ${data.week.name}")
                count++
            }
            if (data.subgroup != 0u.toUByte()) {
                Text("subgroup: ${data.subgroup}")
                count++
            }
            if (data.auditorium.isNotEmpty()) {
                Text("audithorium: ${data.auditorium}")
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
fun PreviewTimeTableLessonCard() {
    NovsuTimeTableTheme(darkTheme = true) {
        Surface {
            Box(modifier = Modifier.fillMaxWidth()) {
                TimeTableScreenLessonCard(
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
                    ),
                    true
                )
            }
        }
    }
}