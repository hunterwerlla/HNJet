package com.hnjet.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.runBlocking
import org.junit.Test

class HNClientTest
{
    @Test
    fun useAppContext() {
        val client = HNClient(HttpClient(Android))
        val response = runBlocking { client.loadPage("https://news.ycombinator.com/") }
        val l =  HNParser.parseStories(response)
        println(l)
    }
}