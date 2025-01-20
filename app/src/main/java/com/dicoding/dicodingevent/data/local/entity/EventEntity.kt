package com.dicoding.dicodingevent.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "event")
data class EventEntity(
    @field:PrimaryKey(autoGenerate = true)
    @field:ColumnInfo(name = "id")
    val id: Int = 0,

    @field:ColumnInfo(name = "name")
    val name: String = "",

    @field:ColumnInfo(name = "mediaCover")
    val mediaCover: String,

    @field:ColumnInfo(name = "beginTime")
    val beginTime: String = "",

    @field:ColumnInfo(name = "link")
    val link: String,

    @field:ColumnInfo(name = "isFavorited")
    var isFavorited: Boolean,
)