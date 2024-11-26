package org.iesharia.minroom2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TareasDao {
    @Query("SELECT * FROM Tareas")
    fun getAll(): List<Tareas>

    @Query("SELECT * FROM TiposTareas")
    fun getAllTipos(): List<TiposTareas>

    @Query("SELECT * FROM Tareas WHERE id=:id")
    fun getTareaById(id: String): Tareas

    @Query("SELECT * FROM TiposTareas WHERE id=:id")
    fun getTipoTareaById(id: String): TiposTareas

    @Query("UPDATE Tareas SET titulo = :titulo, descripcion = :descripcion, tipotareaId=:tipotareaId WHERE id =:id")
    fun updateTarea(titulo: String, descripcion: String, tipotareaId: Int,  id: Int)

    @Query("UPDATE TiposTareas SET titulo = :titulo WHERE id =:id")
    fun updateTipoTarea(titulo: String, id: Int)

    @Insert
    fun insertTarea(vararg tareas: Tareas)

    @Insert
    fun insertTipoTarea(vararg tareas: TiposTareas)

    @Delete
    fun deleteTarea(tarea: Tareas)

    @Delete
    fun deleteTipoTarea(tipotarea: TiposTareas)
}