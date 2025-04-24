package com.adhika.reposcope.data.remote

import com.adhika.reposcope.domain.GitHubRepo
import com.adhika.reposcope.domain.GitHubUser
import com.adhika.reposcope.domain.GitHubUserDetail

fun GitHubUserDto.toDomain() = GitHubUser(
    username = login,
    avatarUrl = avatarUrl
)

fun GitHubUserDetailDto.toDomain() = GitHubUserDetail(
    username = login,
    fullName = name,
    avatarUrl = avatarUrl,
    followers = followers,
    following = following
)

fun GitHubRepoDto.toDomain() = GitHubRepo(
    name = name,
    description = description,
    language = language,
    stars = stars,
    htmlUrl = htmlUrl,
    isForked = isForked
)