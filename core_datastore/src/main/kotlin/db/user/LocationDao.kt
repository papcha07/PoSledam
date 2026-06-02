package db.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocation(locationEntity: LocationEntity)

    @Query("SELECT * FROM location WHERE id = 0 LIMIT 1")
    fun observeLocation(): Flow<LocationEntity?>

    @Query("SELECT * FROM location WHERE id = 0 LIMIT 1")
    suspend fun getLocation(): LocationEntity?
}
