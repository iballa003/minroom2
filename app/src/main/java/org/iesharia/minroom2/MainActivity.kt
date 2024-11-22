package org.iesharia.minroom2


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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

//.fallbackToDestructiveMigration() Usado en caso de que se necesite borrar la base de datos.
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
        val tiposTareasget : List<TiposTareas> = TareasDao.getAllTipos()
        val tareas: List<Tareas> = TareasDao.getAll()
        tareasList = tareas
        tareasTipos = tiposTareasget
        db.close()
    } catch (e: Exception) {
        Log.i("prueba", "Error: $e")
    }
        }
    }
    var openDialog by remember { mutableStateOf(false) }
    if (openDialog) {
        modalWindow({openDialog = false})
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(

        ) {
            Text(text = "Id", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Título", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Descripción", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "Tipo tarea", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(text = "", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }

        LazyColumn(
            modifier = Modifier
        ) {
                items(1) {
                    tareasList?.forEach { tarea ->
                        val tipoTarea = tareasTipos?.firstOrNull { it.id == tarea.tipotareaId }
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = tarea.id.toString(), modifier = Modifier.weight(1f))
                            Text(text = tarea.titulo.toString(), modifier = Modifier.weight(1f))
                            Text(text = tarea.descripcion.toString(), modifier = Modifier.weight(1f))
                            Text(
                                text = tipoTarea?.titulo ?: "Desconocido",
                                modifier = Modifier.weight(1f).padding(start = 20.dp)
                            )
                            IconButton(
                                onClick = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            try {
                                                val TareasDao = db.TareasDao()
                                                val tareaId : Tareas = TareasDao.getTareaById("2")
                                                TareasDao.deleteTarea(tareaId)
                                            }catch (e: Exception){
                                                Log.i("prueba", "Error: $e")
                                            }
                                        }
                                },
                                modifier = Modifier.size(60.dp),
                            ){
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove")
                            }
                            IconButton(
                                onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            val TareasDao = db.TareasDao()
                                            val tareaId : Tareas = TareasDao.getTareaById("2")
                                            TareasDao.updateTarea("Pruebaa","Assssasdadad",2,1)
                                        }catch (e: Exception){
                                            Log.i("prueba", "Error: $e")
                                        }
                                    }
                                },
                                modifier = Modifier.size(60.dp),
                            ){
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Remove")
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
            }

        }
        Button(onClick = {
            openDialog = true
        },
            modifier = modifier.padding(top = 20.dp)) {
            Text(text = "Crear nuevo registro")
        }

    }


//    Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
//        tareasList?.forEach { tarea ->
//            val tipoTarea = tareasTipos?.firstOrNull { it.id == tarea.tipotareaId }
//            Card(
//                modifier = Modifier.size(width = 200.dp, height = 160.dp).padding(top = 15.dp),
//                elevation = CardDefaults.cardElevation(
//                    defaultElevation = 10.dp
//                ),
//                colors = CardDefaults.cardColors(containerColor = Color.LightGray),
//            ){
//                Text(text = "Título de tarea: "+tarea.titulo.toString())
//                Text(text = "Descripción: "+tarea.descripcion.toString())
//                Text(text = "Tipo tarea: ${tipoTarea?.titulo ?: "Desconocido"}")
//            }
//        }
//
//    }

}

@Composable
fun modalWindow(onClose : () -> Unit){
    var text by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    Dialog(onDismissRequest = {  }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                Text(text = "Crear tarea")
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Label") }
                )
                TextField(
                    value = text2,
                    onValueChange = { text2 = it },
                    label = { Text("Label") }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onClose() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onClose() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }

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

    @Query("UPDATE TiposTareas SET titulo = :titulo WHERE id =:id")
    fun updateTipoTarea(titulo: String, id: Int)

    @Query("SELECT * FROM Tareas WHERE titulo LIKE :first AND " +
            "descripcion LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Tareas

    @Insert
    fun insertAll(vararg tareas: Tareas)

    @Insert
    fun insertAllTipos(vararg tareas: TiposTareas)

    @Delete
    fun deleteTarea(tarea: Tareas)

    @Delete
    fun deleteTipoTarea(tipotarea: TiposTareas)
}

@Database(entities = [Tareas::class,TiposTareas::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun TareasDao(): TareasDao
}