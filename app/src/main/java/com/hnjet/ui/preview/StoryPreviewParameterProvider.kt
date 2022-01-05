package com.hnjet.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.hnjet.model.Story
import com.hnjet.util.Random

class StoryPreviewParameterProvider : PreviewParameterProvider<Story> {
    override val values: Sequence<Story> = (1..45).map {
        Story(rank = it, title = "Story $it" + Random.randomString(), voteLink = null)
    }.asSequence()
}
