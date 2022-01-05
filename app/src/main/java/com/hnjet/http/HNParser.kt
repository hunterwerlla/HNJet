package com.hnjet.http

import com.hnjet.model.Story
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

object HNParser {
    fun parseStories(rawHtml: String): List<Story> {
        val parsed = Jsoup.parse(rawHtml)
        // TODO check if fail
        val storeys = parsed.getElementsByClass("athing")
        val points = parsed.getElementsByClass("score")
        // TODO check if size off
        return storeys.map { story ->
            val title = story.getElementsByClass("titleLink").text()
            val voteLink = findVoteLink(story)
            val point = points.find { getStoryIdFromElement(it) == story.id() }
            val rank = story.getElementsByClass("rank").text().substringBefore('.').toInt()
            Story(
                title = title,
                voteLink = voteLink,
                rank = rank
            )
        }
    }

    private fun getStoryIdFromElement(element: Element): String? =
        element.id()
            .split('_')
            .let { if (it.size != 2) null else it }
            ?.get(1)
            ?.let { it.ifBlank { null } }

    private fun findVoteLink(element: Element): String? =
        element.getElementsByClass("votelinks")
            .firstOrNull()
            ?.getElementsByTag("a")
            ?.firstOrNull()
            ?.attributes()
            ?.get("href")
            ?.let { it.ifBlank { null } }
}