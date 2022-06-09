package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.example.stores.mainModule.model.MainInteractor

class MainViewModel: ViewModel() {
    private var storeList: MutableList<StoreEntity>
    private val interactor: MainInteractor

    init {
        storeList = mutableListOf()
        interactor = MainInteractor()
    }

    private val showProgress : MutableLiveData<Boolean> = MutableLiveData()

    private val stores: MutableLiveData<MutableList<StoreEntity>> by lazy{
        MutableLiveData<MutableList<StoreEntity>>().also{
            loadStores()
        }
    }


    fun getStores(): LiveData<MutableList<StoreEntity>>{
        return stores
    }

    fun isShowProgress(): LiveData<Boolean>{
        return showProgress
    }
    private fun loadStores(){
        showProgress.value = Constants.SHOW
        interactor.getStores {
            showProgress.value = Constants.HIDE
            stores.value = it
            storeList = it
        }

    }

    fun deleteStore(storeEntity: StoreEntity){
        interactor.deleteStore(storeEntity,{
            val index = storeList.indexOf(storeEntity)
            if (index != -1) {
                storeList.removeAt(index)
                stores.value = storeList
            }
        })
    }

    fun updateStore(storeEntity: StoreEntity){
        storeEntity.isFavorite = !storeEntity.isFavorite
        interactor.updateStore(storeEntity,{
            val index = storeList.indexOf(storeEntity)
            if (index != -1) {
                storeList.set(index,storeEntity)
                stores.value = storeList
            }
        })
    }

}