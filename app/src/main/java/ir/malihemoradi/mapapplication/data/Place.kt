package ir.malihemoradi.mapapplication.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place")
data class Place(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id:Int=0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name="code")
    val code:String,
    @ColumnInfo(name="east")
    val east:Double,
    @ColumnInfo(name="north")
    val north:Double,
    @ColumnInfo(name="elevation")
    val elevation:Double
)
