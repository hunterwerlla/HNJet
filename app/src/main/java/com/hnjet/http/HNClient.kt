package com.hnjet.http

import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse

class HNClient(private val client: HttpClient) {
    suspend fun loadPage(url: String): String {
        val response: HttpResponse = client.request(url) {}
        return response.receive()
    }
}