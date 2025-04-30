package com.adhika.reposcope.data.remote

import com.adhika.reposcope.domain.model.GitHubRepo
import com.adhika.reposcope.domain.model.GitHubUser
import com.adhika.reposcope.domain.model.GitHubUserDetail

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