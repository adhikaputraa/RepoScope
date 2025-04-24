package com.adhika.reposcope.domain

data class GitHubUser(
    val username: String,
    val avatarUrl: String
)

// domain/model/GitHubUserDetail.kt
data class GitHubUserDetail(
    val username: String,
    val fullName: String?,
    val avatarUrl: String,
    val followers: Int,
    val following: Int
)

// domain/model/GitHubRepo.kt
data class GitHubRepo(
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val htmlUrl: String,
    val isForked: Boolean
)