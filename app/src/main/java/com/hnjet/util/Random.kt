package com.hnjet.util

object Random {
    private val alphaNumeric = ('a'..'z') + ('A'..'Z') + listOf(' ', '\t')

    public fun randomString(): String {
        val length = kotlin.random.Random.nextInt(0, 256)
        return (0..length)
            .map { kotlin.random.Random.nextInt(1, alphaNumeric.size) }
            .map { alphaNumeric[it] }
            .joinToString("")
    }
}