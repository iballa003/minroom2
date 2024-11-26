package org.iesharia.minroom2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.iesharia.minroom2.Tareas
import org.iesharia.minroom2.TareasDao
import org.iesharia.minroom2.TiposTareas

@Database(entities = [Tareas::class,TiposTareas::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun TareasDao():
    TareasDao companion object {
        @Volatile
        private var INSTANCE: org.iesharia.minroom2.AppDatabase? = null
        fun getDatabase(context: Context):
                org.iesharia.minroom2.AppDatabase { return INSTANCE ?:
        synchronized(this) {
            val instance = Room.databaseBuilder( context.applicationContext, org.iesharia.minroom2.AppDatabase::class.java, "task_database" ).build()
            INSTANCE = instance
            instance
        }
        }
    }
}