package wee.digital.log

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CrashItem::class],
    version = 1,
    exportSchema = false
)
abstract class CrashDb : RoomDatabase() {

    abstract val crashDao: CrashItem.DAO

    companion object {

        private val db: CrashDb by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                LogBook.app.applicationContext,
                CrashDb::class.java,
                BuildConfig.LIBRARY_PACKAGE_NAME
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }

        val data: CrashItem.DAO get() = db.crashDao

    }

}