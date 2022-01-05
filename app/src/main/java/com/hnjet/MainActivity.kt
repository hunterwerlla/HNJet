package com.hnjet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hnjet.http.HNClient
import com.hnjet.http.HNParser
import com.hnjet.model.Story
import com.hnjet.ui.preview.StoryPreviewParameterProvider
import com.hnjet.ui.theme.HNJetTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = HNStoriesViewModel(HNClient(HttpClient(Android)))

        setContent {
            HNJetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppBar()
                    LoadStories(viewModel)
                }
            }
        }
    }
}

class HNStoriesSource(
    private val client: HNClient
) : PagingSource<Int, Story>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> =
        try {
            val page = params.key ?: 1
            val html = client.loadPage("https://news.ycombinator.com/news?p=$page")

            val stories = withContext(Dispatchers.IO) {
                HNParser.parseStories(html)
            }
            LoadResult.Page(
                data = stories,
                prevKey = if (page < 1) null else page - 1,
                nextKey = page + 1
            )
        } catch (e: Exception) {
            // TODO handle better
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

class HNStoriesViewModel(private val client: HNClient) : ViewModel() {
    var stories: Flow<PagingData<Story>> = Pager(PagingConfig(pageSize = 30)) {
        HNStoriesSource(client)
    }.flow.cachedIn(viewModelScope)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppBar() {
    MediumTopAppBar(
        title = { Text("TODO title") },
        navigationIcon = {
            // TODO make button do something
            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "TODO fill in description"
                )
            }
        },
        // TODO add colors here
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    )
}

@Composable
fun LoadStories(viewModel: HNStoriesViewModel) {
    val stories: LazyPagingItems<Story> = viewModel.stories.collectAsLazyPagingItems()
    val isRefreshing = stories.loadState.refresh == LoadState.Loading

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { stories.refresh() },
    ) {
        LazyColumn {
            itemsIndexed(stories) { index, it ->
                it?.let {
                    StoryCard(index, it)
                }
            }
        }
        // TODO handle loading and error
    }
}

@Preview
@Composable
fun StoryCard(
    index: Int = 0,
    @PreviewParameter(StoryPreviewParameterProvider::class, 10) story: Story
) {
    Row {
        Column {
            Text(index.toString())
        }
        Column {
            Text(story.title)
        }
    }
}
