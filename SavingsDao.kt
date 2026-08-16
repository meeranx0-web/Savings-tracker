package com.savingstracker.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {

    @Query("SELECT * FROM savings_boxes ORDER BY denomination ASC, id ASC")
    fun observeAllBoxes(): Flow<List<SavingsBox>>

    @Query("SELECT COUNT(*) FROM savings_boxes")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(boxes: List<SavingsBox>)

    @Update
    suspend fun update(box: SavingsBox)

    @Query("DELETE FROM savings_boxes")
    suspend fun deleteAll()
}
