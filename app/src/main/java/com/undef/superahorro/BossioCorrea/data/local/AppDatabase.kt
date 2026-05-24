package com.undef.superahorro.BossioCorrea.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UsuarioEntity::class,
        CompraEntity::class,
        ProductoEntity::class
    ],
    version = 2,                 // ← subimos de 1 a 2 por las tablas nuevas
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun compraDao(): CompraDao
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "superahorro.db"
                )
                    .fallbackToDestructiveMigration()  // en dev: borra y recrea si cambia la versión
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}