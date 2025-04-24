package com.adhika.reposcope.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUserSearchResponse(
    @SerialName("items") val users: List<GitHubUserDto>
)

@Serializable
data class GitHubUserDto(
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)

@Serializable
data class GitHubUserDetailDto(
    @SerialName("login") val login: String,
    @SerialName("name") val name: String? = null,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("followers") val followers: Int,
    @SerialName("following") val following: Int
)

@Serializable
data class GitHubRepoDto(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("stargazers_count") val stars: Int,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("fork") val isForked: Boolean
)