# # Aplicación de tareas
Un proyecto sencillo que usa Room e incorpora una base de datos que guarda las tablas Tareas y TiposTareas. Ambas tablas tienen funcionalidad CRUD completa y están relacionadas entre ellas.


##  Requisitos

-   Una computadora con Android Studio.
-   Un dispositivo o un emulador con nivel de API 35 o posterior.
-  Tener todas las dependencias de room y KSP.

## Descripción general de la app

Esta App permite mostrar, crear, actualizar y borrar tareas y tipos tareas de la base de datos.
Como en la foreign key incorpora borrado en cascada, si borras un tipo de tarea que esté en una o más tareas, estos se borrarán juntos.

## Componentes principales de Room
Kotlin ofrece una manera fácil de trabajar con datos a través de clases de datos. Si bien es fácil trabajar con datos en la memoria mediante clases de datos, cuando se trata de datos persistentes, debes convertirlos en un formato compatible con el almacenamiento de bases de datos. De este modo, necesitas  _tablas_  para almacenar los datos y  _consultas_  para acceder a ellos y modificarlos.
