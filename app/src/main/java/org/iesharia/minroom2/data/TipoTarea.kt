package org.iesharia.minroom2.data


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class TiposTareas(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // autoGenerate habilitado
    @ColumnInfo(name = "titulo") val titulo: String?,
)
