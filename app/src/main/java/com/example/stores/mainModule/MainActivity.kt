package com.example.stores.mainModule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.*
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.MainAux
import com.example.stores.databinding.ActivityMainBinding
import com.example.stores.editModule.EditStoreFragment
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.adapter.OnClickListener
import com.example.stores.mainModule.adapter.StoreAdapter
import com.example.stores.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener{

   private lateinit var mBinding: ActivityMainBinding

   private lateinit var mAdapter: StoreAdapter
   private lateinit var mGrideLayout: GridLayoutManager

   //MVVM
   private lateinit var mMainViewModel: MainViewModel
   private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.fab.setOnClickListener { lauchEditFragment() }
        setupViewModel()
        setupRecylerView()
    }

    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mMainViewModel.getStores().observe(this,{ stores ->
            mAdapter.setStores(stores)
        })

        mEditStoreViewModel = ViewModelProvider(this).get(EditStoreViewModel::class.java)
        mEditStoreViewModel.getShowFab().observe(this,{
            isVisible ->
            if(isVisible) mBinding.fab.show() else mBinding.fab.hide()
        })
        mEditStoreViewModel.getStoreSelected().observe(this,{ storeEntity ->
            mAdapter.add(storeEntity)
        })
    }

    private fun lauchEditFragment(storeEntity: StoreEntity = StoreEntity()) {

        mEditStoreViewModel.setShowFab(false)
        mEditStoreViewModel.setStoreSelected(storeEntity)
        val fragment = EditStoreFragment()

        val fragmentManager = supportFragmentManager
        val fragmentransaction = fragmentManager.beginTransaction()

        fragmentransaction.add(R.id.containerMain, fragment)
        fragmentransaction.commit()
        fragmentransaction.addToBackStack(null)
    }

    private fun setupRecylerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGrideLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_colums))

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGrideLayout
            adapter = mAdapter
        }
    }




    /*
    *OnClickListener
    * */
    override fun onClick(storeEntity: StoreEntity) {
        lauchEditFragment(storeEntity)

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        mMainViewModel.updateStore(storeEntity)
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_optios_item)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_optios_title)
            .setItems(items,  { dialogInterface, i ->
                when (i){
                    0 -> confirmDelete(storeEntity)

                    1 -> dial(storeEntity.phone)

                    2 -> gotoWebSite(storeEntity.website)
                }
            })
            .show()

    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_alert_delete)
            .setPositiveButton(R.string.dialog_delete_confirm,{ dialogInterface, i ->
                mMainViewModel.deleteStore(storeEntity)
            })
            .setNegativeButton(R.string.dialog_alert_cancel,null)
            .show()
    }

    private fun dial(phone:String){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }

       startIntent(callIntent)
    }

    private fun gotoWebSite(webSite:String){
        if(webSite.isEmpty()){
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        }else{
            val webSiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(webSite)
            }
            startIntent(webSiteIntent)
        }
        }
    private fun startIntent(intent: Intent){
        if(intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
    }

//

}