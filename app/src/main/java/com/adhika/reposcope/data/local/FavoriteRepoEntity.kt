package com.adhika.reposcope.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_repos")
data class FavoriteRepoEntity(
    @PrimaryKey val htmlUrl: String, // unique repo URL
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val isForked: Boolean,
    val owner: String
)
