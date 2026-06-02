package db.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationEntity(
    @PrimaryKey
    val id: Int = 0,
    val latitude: Double,
    val longitude: Double
)