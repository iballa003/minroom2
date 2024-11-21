package org.iesharia.minroom2


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
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

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val db = Room.databaseBuilder(
        LocalContext.current,
        AppDatabase::class.java, "database-name"
    ).build()
    var tareasList by remember { mutableStateOf<List<Tareas>?>(null) }
    LaunchedEffect(key1 = {}) {
        CoroutineScope(Dispatchers.IO).launch {
    try {
        val TareasDao = db.TareasDao()
        //val tarea = Tareas(6, "Kotlin", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
//        Log.i("prueba", "Para insertar")
//        TareasDao.insertAll(tarea)
        val tareas: List<Tareas> = TareasDao.getAll()
        tareasList = tareas
        Log.i("prueba", "Tareas: $tareas")
    } catch (e: Exception) {
        Log.i("prueba", "Error: $e")
    }
        }
    }

    Column {
        tareasList?.forEach { item ->
            Text(text = "Título de tarea: "+item.titulo.toString())
            Text(text = "Descripción: "+item.descripcion.toString())
        }

    }

}

@Entity
data class Tareas(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "titulo") val titulo: String?,
    @ColumnInfo(name = "descripcion") val descripcion: String?
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

    @Query("SELECT * FROM Tareas WHERE id IN (:tareas)")
    fun loadAllByIds(tareas: IntArray): List<Tareas>

    @Query("SELECT * FROM Tareas WHERE titulo LIKE :first AND " +
            "descripcion LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Tareas

    @Insert
    fun insertAll(vararg tareas: Tareas)

    @Delete
    fun delete(tarea: Tareas)
}

@Database(entities = [Tareas::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun TareasDao(): TareasDao
}