package org.iesharia.minroom2.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TiposTareas(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "titulo") val titulo: String?,
)