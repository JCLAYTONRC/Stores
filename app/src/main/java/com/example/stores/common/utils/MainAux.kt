package com.example.stores.common.utils

import com.example.stores.common.entities.StoreEntity

interface MainAux {
    fun hidefab(isVisible:Boolean = false)

    fun addStore(storeEntity: StoreEntity)
    fun updateStore(storeEntity: StoreEntity)
}