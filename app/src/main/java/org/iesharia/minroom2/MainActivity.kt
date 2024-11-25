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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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


// https://stackoverflow.com/questions/67111020/exposed-drop-down-menu-for-jetpack-compose
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
    var tareaView by remember { mutableStateOf(true) }
    var openDialog by remember { mutableStateOf(false) }
    if (openDialog) {
        ModalWindow("Crear", {openDialog = false}, db)
    }
    LaunchedEffect(key1 = {}) {// Solo se ejecuta una vez
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val TareasDao = db.TareasDao()
                val tiposTareasget : List<TiposTareas> = TareasDao.getAllTipos()
                val tareas: List<Tareas> = TareasDao.getAll()
                tareasList = tareas
                tareasTipos = tiposTareasget
                Log.i("DAM2", "Tareas: $tareas")
            } catch (e: Exception) {
                Log.i("prueba", "Error: $e")
            }
        }
    }


    Column(modifier = modifier.padding(start = 15.dp, end = 15.dp, top = 30.dp)) {
        Row {
            Button(onClick = {tareaView = true}, enabled = !tareaView) {
                Text(text = "Tareas")
            }
            Button(onClick = {tareaView = false}, enabled = tareaView) {
                Text(text = "Tipos Tareas")
            }
        }
        if (tareaView){
            tareasList?.forEach { tarea ->
                Log.i("DAM2", tarea.id.toString())
                TareaCard(tarea,db,tareasTipos, tarea.id)
            }
        }else{
            tareasTipos?.forEach { tipotarea ->
                Log.i("DAM2", tipotarea.id.toString())
                TipoTareaCard(tipotarea,db, tipotarea.id)
            }
        }
        Button(onClick = {
            Log.i("DAM2","crear")
            openDialog = true
        },
            modifier = Modifier.padding(top = 20.dp)
        ) {

            Text(text = if (tareaView)"Crear nueva tarea" else "Crear nuevo tipo tarea")
        }
        //createTareaCards(tareasList,db,tareasTipos, {updateList = true})
    }


}


@Composable
fun createTareaCards(tareasList: List<Tareas>?, database: AppDatabase, tareasTipos: List<TiposTareas>?, finished : () -> Unit){
    var openDialog by remember { mutableStateOf(false) }
    if (openDialog) {
        ModalWindow("Crear", {openDialog = false}, database)
    }
    tareasList?.forEach { tarea ->
        Log.i("DAM2", tarea.id.toString())
        TareaCard(tarea,database,tareasTipos, tarea.id)
    }
    Button(onClick = {
        Log.i("DAM2","start")
        openDialog = true
        finished()
    },
        modifier = Modifier.padding(top = 20.dp)
    ) {
        Text(text = "Crear nueva tarea")
    }
}


@Composable
fun TareaCard(tarea : Tareas, database: AppDatabase, tiposTareas: List<TiposTareas>?, id : Int){
    val tipoTarea = tiposTareas?.firstOrNull { it.id == tarea.tipotareaId }
    var alertWindow by remember { mutableStateOf(false) }
    var showCard by remember { mutableStateOf(true) }
    val index : Int = id
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        ModalWindow("Editar",
            {openDialog = false},
            database,
            index.toString()
        )
    }
    if (alertWindow){
        AlertDialogModal(
            {//Al darle a cancelar
                alertWindow = false
            },
            {//Al darle a confirmar
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val TareasDao = database.TareasDao()
                        val tareaId : Tareas = TareasDao.getTareaById(index.toString())
                        TareasDao.deleteTarea(tareaId)
                        showCard = false
                    }catch (e: Exception){
                        Log.i("prueba", "Error: $e")
                    }
                }
                alertWindow = false
            },
            dialogTitle = "¿Seguros que quieres borrar?",
            dialogText ="Una vez borrado, no podrás recuperarlo.",
            icon = Icons.Default.Info
        )
    }// Fin del if
    if(showCard){
        Card(
            modifier = Modifier
                .size(width = 350.dp, height = 160.dp)
                .padding(top = 15.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        ){
            Text(text = "Título de tarea: "+tarea.titulo)
            Row {
                Text(text = "Descripción: "+tarea.descripcion.toString())
                IconButton(
                    onClick = {
                        alertWindow = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .weight(1f),
                ){
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove")
                }
                IconButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                openDialog = true
                            }catch (e: Exception){
                                Log.i("prueba", "Error: $e")
                            }
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .weight(1f),
                ){
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                }


            }


            Text(text = "Tipo tarea: ${tipoTarea?.titulo ?: "Desconocido"}")
        }
    }
}

@Composable
fun TipoTareaCard(tarea : TiposTareas, database: AppDatabase, id : Int){
    var alertWindow by remember { mutableStateOf(false) }
    var showCard by remember { mutableStateOf(true) }
    val index : Int = id
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        ModalWindow("Editar",
            {openDialog = false},
            database,
            index.toString()
        )
    }
    if (alertWindow){
        AlertDialogModal(
            {//Al darle a cancelar
                alertWindow = false
            },
            {//Al darle a confirmar
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val TareasDao = database.TareasDao()
                        val tareaId : TiposTareas = TareasDao.getTipoTareaById(index.toString())
                        TareasDao.deleteTipoTarea(tareaId)
                        showCard = false
                    }catch (e: Exception){
                        Log.i("prueba", "Error: $e")
                    }
                }
                alertWindow = false
            },
            dialogTitle = "¿Seguros que quieres borrar?",
            dialogText ="Una vez borrado, no podrás recuperarlo.",
            icon = Icons.Default.Info
        )
    }// Fin del if
    if(showCard){
        Card(
            modifier = Modifier
                .size(width = 350.dp, height = 160.dp)
                .padding(top = 15.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        ){
            Spacer(Modifier.height(10.dp))
            Row {
                Text(text = "Título del tipo de tarea: "+tarea.titulo)
                IconButton(
                    onClick = {
                        alertWindow = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .weight(1f),
                ){
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove")
                }
                IconButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                openDialog = true
                            }catch (e: Exception){
                                Log.i("prueba", "Error: $e")
                            }
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .weight(1f),
                ){
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                }


            }
        }
    }
}

@Composable
fun ModalWindow(modalTitulo : String, onClose : () -> Unit, database: AppDatabase, index: String = "1"){
    var id by remember { mutableStateOf("0") }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipotarea by remember { mutableStateOf("0") }

    Dialog(onDismissRequest = {  }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                Text(text = modalTitulo+ " tarea", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(5.dp))
                if (modalTitulo == "Crear"){
                    TextField(
                        value = id,
                        onValueChange = { id = it },
                        label = { Text("Id") }
                    )
                }
                TextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Titulo") }
                )
                TextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripcion") },
                    modifier = Modifier.height(100.dp)
                )
                TextField(
                    value = tipotarea,
                    onValueChange = { tipotarea = it },
                    label = { Text("Tipo tarea") },
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(// Para cancelar
                        onClick = { onClose() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Cancelar")
                    }
                    TextButton(//Para confirmar
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    if (modalTitulo == "Crear"){
                                        val tarea = Tareas(id.toInt(),titulo, descripcion, tipotarea.toInt())
                                        database.TareasDao().insertTarea(tarea)
                                    }else{
                                        Log.i("DAM2","Editar")
                                        Log.i("DAM2", "Indice a actualizar: "+index.toString())
                                        database.TareasDao().updateTarea(titulo,descripcion,tipotarea.toInt(),index.toInt())
                                    }
                                }catch (e: Exception){
                                    Log.i("prueba", "Error: $e")
                                }}


                            onClose()
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirmar")
                    }
                }


            }
        }
    }
}


@Composable
fun AlertDialogModal(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancelar")
            }
        }
    )
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

    @Query("SELECT * FROM TiposTareas WHERE id=:id")
    fun getTipoTareaById(id: String): TiposTareas


    @Query("UPDATE Tareas SET titulo = :titulo, descripcion = :descripcion, tipotareaId=:tipotareaId WHERE id =:id")
    fun updateTarea(titulo: String, descripcion: String, tipotareaId: Int,  id: Int)


    @Query("UPDATE TiposTareas SET titulo = :titulo WHERE id =:id")
    fun updateTipoTarea(titulo: String, id: Int)


    @Query("SELECT * FROM Tareas WHERE titulo LIKE :first AND " +
            "descripcion LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Tareas


    @Insert
    fun insertTarea(vararg tareas: Tareas)


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
