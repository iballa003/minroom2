package org.iesharia.minroom2


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.iesharia.minroom2.ui.theme.Minroom2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.i("prueba", "Inicio")


        setContent {
            Minroom2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

//.fallbackToDestructiveMigration()
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val db = Room.databaseBuilder(
        LocalContext.current,
        AppDatabase::class.java, "database-name"
    ).build()

    var tareasList by remember { mutableStateOf<List<Tareas>?>(null) }
    var tareasTipos by remember { mutableStateOf<List<TiposTareas>?>(null) }

    LaunchedEffect(key1 = {}) {
        CoroutineScope(Dispatchers.IO).launch {
    try {
        val TareasDao = db.TareasDao()
        //val tipotarea = TiposTareas(2,"Dificil")
        //val tarea = Tareas(2, "Kotlin", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",2)
        //TareasDao.insertAllTipos(tipotarea)
        //TareasDao.insertAll(tarea)
//        Log.i("prueba", "Para insertar")
        //TareasDao.insertAllTipos(tipotarea)
        val tiposTareasget : List<TiposTareas> = TareasDao.getAllTipos()
        val tareas: List<Tareas> = TareasDao.getAll()
        //val tareasId: List<Tareas> = TareasDao.loadAllByIds(intArrayOf(1))
        val tareaId : Tareas = TareasDao.getTareaById("2")
        tareasList = tareas
        tareasTipos = tiposTareasget
        Log.i("prueba", "Tareas: $tareaId")
        TareasDao.delete(tareaId)
        Log.i("prueba", "Tareas: $tareaId")
        db.close()
    } catch (e: Exception) {
        Log.i("prueba", "Error: $e")
    }
        }
    }

    Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
        tareasList?.forEach { tarea ->
            val tipoTarea = tareasTipos?.firstOrNull { it.id == tarea.tipotareaId }
            Card(
                modifier = Modifier.size(width = 200.dp, height = 160.dp).padding(top = 15.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray),
            ){
                Text(text = "Título de tarea: "+tarea.titulo.toString())
                Text(text = "Descripción: "+tarea.descripcion.toString())
                Text(text = "Tipo tarea: ${tipoTarea?.titulo ?: "Desconocido"}")
            }
        }

    }

}


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

@Entity
data class TiposTareas(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "titulo") val titulo: String?,
)

@Dao
interface TareasDao {
    @Query("SELECT * FROM Tareas")
    fun getAll(): List<Tareas>

    @Query("SELECT * FROM TiposTareas")
    fun getAllTipos(): List<TiposTareas>

    @Query("SELECT * FROM Tareas WHERE id IN (:tareas)")
    fun loadAllByIds(tareas: IntArray): List<Tareas>

    @Query("SELECT * FROM Tareas WHERE id=:id")
    fun getTareaById(id: String): Tareas

    @Query("UPDATE Tareas SET titulo = :titulo, descripcion = :descripcion, tipotareaId=:tipotareaId WHERE id =:id")
    fun updateTarea(titulo: String, descripcion: String, tipotareaId: Int,  id: Int)

    @Query("SELECT * FROM Tareas WHERE titulo LIKE :first AND " +
            "descripcion LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Tareas

    @Insert
    fun insertAll(vararg tareas: Tareas)

    @Insert
    fun insertAllTipos(vararg tareas: TiposTareas)

    @Delete
    fun delete(tarea: Tareas)
}

@Database(entities = [Tareas::class,TiposTareas::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun TareasDao(): TareasDao
}