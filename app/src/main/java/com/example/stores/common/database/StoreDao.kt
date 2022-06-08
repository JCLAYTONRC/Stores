package com.example.stores.common.database

import androidx.room.*
import com.example.stores.common.entities.StoreEntity

@Dao
interface StoreDao {
    @Query("SELECT * FROM StoreEntity")
    fun getAllStores() : MutableList<StoreEntity>

    @Insert
    fun addStore(storeEntity: StoreEntity) :Long

    @Update
    fun updateStore(storeEntity: StoreEntity)

    @Delete
    fun deleteStore(storeEntity: StoreEntity)

    @Query("SELECT * FROM StoreEntity WHERE id = :id")
    fun getStoreById(id: Long) : StoreEntity

}