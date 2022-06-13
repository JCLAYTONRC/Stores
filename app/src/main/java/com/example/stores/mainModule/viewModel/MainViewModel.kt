package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.example.stores.common.utils.StoresException
import com.example.stores.common.utils.TypeError
import com.example.stores.mainModule.model.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val interactor: MainInteractor = MainInteractor()

    private val showProgress : MutableLiveData<Boolean> = MutableLiveData()
    private val typeError : MutableLiveData<TypeError> = MutableLiveData()


    private val stores = interactor.stores

    fun getStores(): LiveData<MutableList<StoreEntity>>{
        return stores
    }
    fun getTypeError(): MutableLiveData<TypeError> = typeError


    fun isShowProgress(): LiveData<Boolean>{
        return showProgress
    }

    fun deleteStore(storeEntity: StoreEntity){

        executeAction { interactor.deleteStore(storeEntity)
        }
    }

    fun updateStore(storeEntity: StoreEntity){

        storeEntity.isFavorite =! storeEntity.isFavorite
        executeAction { interactor.updateStore(storeEntity) }

    }

    private fun executeAction(block: suspend () -> Unit) : Job{
        return  viewModelScope.launch {
            showProgress.value = Constants.SHOW

            try{
                block()
            } catch (e: StoresException){
                typeError.value = e.typeError
            } finally {
                showProgress.value = Constants.HIDE
            }
        }
        }

}