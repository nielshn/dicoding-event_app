package com.dicoding.dicodingevent.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detailEvent")

data class DetailEventEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "mediaCover")
    val mediaCover: String,

    @ColumnInfo(name = "beginTime")
    val beginTime: String = "",

    @ColumnInfo(name = "link")
    val link: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "ownerName")
    val ownerName: String,

    @ColumnInfo(name = "cityName")
    val cityName: String,

    @ColumnInfo(name = "quota")
    val quota: Int = 0,

    @ColumnInfo(name = "registrants")
    val registrants: Int,

)