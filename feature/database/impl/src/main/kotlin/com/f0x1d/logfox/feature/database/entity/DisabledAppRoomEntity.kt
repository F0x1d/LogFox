package com.f0x1d.logfox.feature.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DisabledApp")
internal data class DisabledAppRoomEntity(
    @ColumnInfo(name = "package_name", index = true) val packageName: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
)
