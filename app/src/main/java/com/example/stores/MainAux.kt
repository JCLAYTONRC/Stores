package com.example.stores

interface MainAux {
    fun hidefab(isVisible:Boolean = false)

    fun addStore(storeEntity: StoreEntity)
    fun updateStore(storeEntity: StoreEntity)
}