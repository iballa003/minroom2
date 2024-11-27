package org.iesharia.minroom2.view


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.iesharia.minroom2.data.AppDatabase
import org.iesharia.minroom2.data.Tareas
import org.iesharia.minroom2.data.TiposTareas




@Composable
fun tareaApp(
    database: AppDatabase,
    modifier: Modifier = Modifier
) {


    var tareasList by remember { mutableStateOf<List<Tareas>?>(null) }
    var tareasListTipo : MutableList<MutableList<String>> = mutableListOf()
    var tareasTipos by remember { mutableStateOf<List<TiposTareas>?>(null) }
    var tareaView by remember { mutableStateOf(true) }
    var openDialog by remember { mutableStateOf(false) }


    // Función para cargar tareas
    fun loadTareas() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val TareasDao = database.TareasDao()
                val tareas = TareasDao.getAll()
                val tiposTareas = TareasDao.getAllTipos()
                tareasList = tareas
                tareasTipos = tiposTareas
                Log.i("DAM2", "Tareas: $tareas")
            } catch (e: Exception) {
                Log.i("prueba", "Error: $e")
            }
        }
    }


    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        loadTareas()
    }


    tareasTipos?.forEach { tipotarea ->
        tareasListTipo.add(mutableListOf((tipotarea.id).toString(), tipotarea.titulo?:"Nada"))
    }


    Log.i("DAM23", tareasListTipo.toString())
    if (openDialog) {
        ModalWindow(modalTitulo = if(tareaView) "Crear Tarea" else "Crear tipo Tarea",
            onClose = {openDialog = false},
            database = database,
            onTaskCreated = { loadTareas() },
            index = "1",
            listDropdown = tareasListTipo
        )
    }
    Column(modifier = modifier.padding(start = 15.dp, end = 5.dp, top = 25.dp)) {
        Row {
            Button(
                onClick = { tareaView = true },
                enabled = !tareaView,
                modifier = modifier.weight(1f)
            ) {
                Text(text = "Tareas")
            }
            Button(
                onClick = { tareaView = false },
                enabled = tareaView,
                modifier = modifier.weight(1f)
            ) {
                Text(text = "Tipos Tareas")
            }
        }




        Column(modifier = modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp)
            .verticalScroll(rememberScrollState())) {
            if (tareaView){
                tareasList?.forEach { tarea ->
                    Log.i("DAM2", tarea.id.toString())
                    TareaCard(tarea,database,tareasTipos, tarea.id, tareasListTipo, {loadTareas()})
                }
            }else{
                tareasTipos?.forEach { tipotarea ->
                    Log.i("DAM2", tipotarea.id.toString())
                    TipoTareaCard(tipotarea,database, tipotarea.id, {loadTareas()})
                }
            }
            Button(onClick = {
                Log.i("DAM2","crear")
                openDialog = true
            },
                modifier = Modifier.padding(top = 15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(text = if (tareaView)"Crear nueva tarea" else "Crear nuevo tipo tarea")
            }
        }
    }
}




@Composable
fun TareaCard(
    tarea : Tareas,
    database: AppDatabase,
    tiposTareas: List<TiposTareas>?,
    id : Int,
    listDropdownTareas : List<List<String>> = mutableListOf<MutableList<String>>(),
    onTaskDeleted: () -> Unit // Callback para recargar la lista
)
{
    val tipoTarea = tiposTareas?.firstOrNull { it.id == tarea.tipotareaId }
    var alertWindow by remember { mutableStateOf(false) }
    var showCard by remember { mutableStateOf(true) }
    val index : Int = id
    var openDialog by remember { mutableStateOf(false) }




    if (openDialog) {
        ModalWindow(modalTitulo = "Editar Tarea",
            onClose = {openDialog = false},
            database = database,
            onTaskCreated = {onTaskDeleted()}, // Callback para actualizar tareas
            index = index.toString(),
            listDropdown = listDropdownTareas
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
                        onTaskDeleted()
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
                .padding(top = 10.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        ){
            Text(text = "Título de tarea: "+tarea.titulo, fontWeight = FontWeight.Bold)
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
fun TipoTareaCard(
    tarea : TiposTareas,
    database: AppDatabase,
    id : Int,
    onTaskDeleted: () -> Unit // Callback para recargar la lista
)
{
    var alertWindow by remember { mutableStateOf(false) }
    var showCard by remember { mutableStateOf(true) }
    val index : Int = id
    var openDialog by remember { mutableStateOf(false) }


    if (openDialog) {
        ModalWindow("Editar Tipo Tarea",
            onClose = {openDialog = false},
            database = database,
            onTaskCreated = {onTaskDeleted()}, // Callback para actualizar tareas
            index = index.toString()
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
                        onTaskDeleted()
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








@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalWindow(
    modalTitulo : String,
    onClose : () -> Unit,
    database: AppDatabase,
    onTaskCreated: (() -> Unit)? = null, // Callback para actualizar tareas
    index: String = "1",
    listDropdown : List<List<String>> = mutableListOf<MutableList<String>>()
)
{
    var id by remember { mutableStateOf("1") }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipotarea by remember { mutableStateOf("1") }
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }
    var selectedOptionIndex by remember { mutableStateOf("") }




    if (modalTitulo != "Crear tipo Tarea" && modalTitulo != "Editar Tipo Tarea"){
        selectedOptionText = listDropdown[0][1]
        selectedOptionIndex = listDropdown[0][0]
    }




    Dialog(onDismissRequest = {  }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                Text(text = modalTitulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(5.dp))
                if (modalTitulo == "Crear Tarea" || modalTitulo == "Crear tipo Tarea"){
                    TextField(
                        value = id,
                        onValueChange = { id = it },
                        label = { Text("Id") },
                        isError = id.isEmpty()
                    )
                }
                TextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Titulo") },
                    isError = titulo.isEmpty()
                )
                if (modalTitulo != "Crear tipo Tarea" && modalTitulo != "Editar Tipo Tarea"){
                    TextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripcion") },
                        modifier = Modifier.height(100.dp),
                        isError = descripcion.isEmpty()
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        TextField(
                            readOnly = true,
                            value = selectedOptionText,
                            onValueChange = {},
                            label = { Text("Tipo tarea") },
                            isError = tipotarea.isEmpty(),
                            modifier = Modifier.menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
                        ) {
                            listDropdown.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(text = selectionOption[1]) },
                                    onClick = {
                                        selectedOptionText = selectionOption[1]
                                        selectedOptionIndex = selectionOption[0]
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
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
                                    when (modalTitulo) {
                                        "Crear Tarea" -> {
                                            if(id.isNotEmpty() && titulo.isNotEmpty() && descripcion.isNotEmpty() && tipotarea.isNotEmpty()){
                                                Log.i("DAM2","Creado")
                                                val indexValue = selectedOptionIndex
                                                Log.i("DAM2", "Indice a crear: "+indexValue)
                                                val tarea = Tareas(id.toInt(),titulo, descripcion, indexValue.toInt())
                                                database.TareasDao()
                                                    .insertTarea(tarea)
                                                onTaskCreated?.invoke() // Actualizar la lista
                                                onClose()
                                            }
                                        }
                                        "Editar Tarea" -> {
                                            if(index.isNotEmpty() && titulo.isNotEmpty() && descripcion.isNotEmpty() && tipotarea.isNotEmpty()){
                                                Log.i("DAM2","Editar")


                                                val indexValue = selectedOptionIndex
                                                Log.i("DAM2", "Indice a actualizar: "+indexValue)
                                                database.TareasDao()
                                                    .updateTarea(titulo,descripcion,
                                                        indexValue.toInt(),index.toInt())
                                                onTaskCreated?.invoke() // Actualizar la lista
                                                onClose()
                                            }
                                        }
                                        "Crear tipo Tarea" -> {
                                            if(id.isNotEmpty() && titulo.isNotEmpty()){
                                                val tipoTarea = TiposTareas(id.toInt(),titulo)
                                                database.TareasDao()
                                                    .insertTipoTarea(tipoTarea)
                                                onTaskCreated?.invoke() // Actualizar la lista
                                                onClose()
                                            }
                                        }
                                        else -> {
                                            if(index.isNotEmpty() && titulo.isNotEmpty()) {
                                                Log.i("DAM2", "Editar")
                                                Log.i("DAM2", "Indice a actualizar: " + index)
                                                database.TareasDao()
                                                    .updateTipoTarea(titulo, index.toInt())
                                                onTaskCreated?.invoke() // Actualizar la lista
                                                onClose()
                                            }
                                        }
                                    }
                                }catch (e: Exception){
                                    Log.i("DAM2", "Error: $e")
                                }}
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

