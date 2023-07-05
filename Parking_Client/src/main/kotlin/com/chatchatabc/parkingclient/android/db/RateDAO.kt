package com.chatchatabc.parkingclient.android.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.chatchatabc.parkingclient.android.db.entity.RateDBObject

@Dao
interface RateDAO {
    @Query("SELECT * FROM ratedbobject WHERE id = :rateId")
    abstract fun getRateById(rateId: String): RateDBObject

    @Insert
    fun insertRate(rate: RateDBObject)

    @Transaction
    fun addAll(rates: List<RateDBObject>) {
        rates.forEach { insertRate(it) }
    }

    @Delete
    fun deleteRate(rate: RateDBObject)
}