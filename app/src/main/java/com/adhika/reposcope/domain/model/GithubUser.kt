// Domain model for GithubUser
package com.adhika.reposcope.domain.model

data class GitHubUser(
    val username: String,
    val avatarUrl: String
)

data class GitHubUserDetail(
    val username: String,
    val fullName: String?,
    val avatarUrl: String,
    val followers: Int,
    val following: Int
)

data class GitHubRepo(
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val htmlUrl: String,
    val isForked: Boolean
)
