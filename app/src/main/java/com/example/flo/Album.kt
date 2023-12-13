package com.example.flo

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "AlbumTable")
data class Album(
    var title: String?="",
    var singer: String?="",
    var coverImg: Int?=null,
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
)
