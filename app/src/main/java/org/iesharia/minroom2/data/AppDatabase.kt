package org.iesharia.minroom2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Tareas::class,TiposTareas::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun TareasDao():
    TareasDao companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context):
                AppDatabase { return INSTANCE ?:
        synchronized(this) {
            val instance = Room.databaseBuilder( context.applicationContext, AppDatabase::class.java, "database-name" ).build()
            INSTANCE = instance
            instance
        }
        }
    }
}

