package org.iesharia.minroom2.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity (
    foreignKeys = [
        ForeignKey(
            entity = TiposTareas::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("tipotareaId"),
            onDelete = ForeignKey.CASCADE // Esto asegura que cuando un TipoTarea se elimine, se eliminen las tareas asociadas
        )
    ]
)
data class Tareas(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "titulo") val titulo: String?,
    @ColumnInfo(name = "descripcion") val descripcion: String?,
    @ColumnInfo(name = "tipotareaId") val tipotareaId: Int?
)