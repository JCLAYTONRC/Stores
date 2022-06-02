package com.example.stores

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.sql.RowId

class MainActivity : AppCompatActivity(), OnClickListener,MainAux {

   private lateinit var mBinding: ActivityMainBinding

   private lateinit var mAdapter: StoreAdapter
   private lateinit var mGrideLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

       /* mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mBinding.etName.text.toString().trim())

            Thread{
                StoreApplication.database.storeDao().addStore(store)
            }.start()

            mAdapter.add(store)
        }*/

        mBinding.fab.setOnClickListener { lauchEditFragment() }

        setupRecylerView()
    }

    private fun lauchEditFragment(args : Bundle? = null) {
        val fragment = EditStoreFragment()
        if(args != null) fragment.arguments = args

        val fragmentManager = supportFragmentManager
        val fragmentransaction = fragmentManager.beginTransaction()

        fragmentransaction.add(R.id.containerMain, fragment)
        fragmentransaction.commit()
        fragmentransaction.addToBackStack(null)
        hidefab()
    }

    private fun setupRecylerView() {
        mAdapter = StoreAdapter(mutableListOf(),this)
        mGrideLayout = GridLayoutManager(this,resources.getInteger(R.integer.main_colums))
        getStores()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGrideLayout
            adapter = mAdapter
        }
    }

    private fun getStores(){
        doAsync {
            val stores = StoreApplication.database.storeDao().getAllStores()
            uiThread {
                mAdapter.setStores(stores)
            }
        }
    }



    /*
    *OnClickListener
    * */
    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id),storeId)

        lauchEditFragment(args)

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                updateStore(storeEntity)
            }
        }
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
                doAsync {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                    uiThread {
                        mAdapter.delete(storeEntity)
                    }
                }
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
            Toast.makeText(this,R.string.main_error_no_website,Toast.LENGTH_LONG).show()
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
            Toast.makeText(this,R.string.main_error_no_resolve,Toast.LENGTH_LONG).show()
    }


    override fun hidefab(isVisible: Boolean) {
        if(isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}