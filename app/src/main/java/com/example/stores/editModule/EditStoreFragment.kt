package com.example.stores.editModule

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.FragmentEditStoreBinding
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding

    //MVVM
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    private var mActivity: MainActivity? = null
    private var misEditMode : Boolean = false
    private  lateinit var mStoreEntity : StoreEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditStoreViewModel = ViewModelProvider(requireActivity()).get(EditStoreViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //MVVM
        setUpViewModel()

        setUpTextFields()

    }

    private fun setUpViewModel() {
        mEditStoreViewModel.getStoreSelected().observe(viewLifecycleOwner,{
            mStoreEntity = it
            if(it.id != 0L){
                misEditMode = true
                setUiStore(it)
            }else{
                misEditMode = false
            }

            setUpActionBar()
        })

        mEditStoreViewModel.getResult().observe(viewLifecycleOwner,{ result ->
            hideKeyboard()

            when(result){
                is Long ->{
                    mStoreEntity!!.id = result

                    mEditStoreViewModel.setStoreSelected(mStoreEntity)

                    Toast.makeText(mActivity,
                        R.string.edit_store_message_save_success,Toast.LENGTH_SHORT).show()

                    mActivity?.onBackPressed()
                }
                is StoreEntity ->{
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)

                    Snackbar.make(mBinding.root,
                        R.string.edit_store_message_update_success,Snackbar.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun setUpActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if(misEditMode) getString(R.string.edit_store_title_edit)
        else getString(R.string.edit_store_title_add)
        setHasOptionsMenu(true)

    }

    private fun setUpTextFields() {
        with(mBinding){
            etName.addTextChangedListener { (tilName) }
            etPhone.addTextChangedListener { (tilPhone) }
            etPhotoUrl.addTextChangedListener { (tilPhotoUrl)
                loadImage(it.toString().trim())}
        }

    }

    private fun loadImage(url: String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding){
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etWebsite.text = storeEntity.website.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()


        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                hideKeyboard()
                true
            }
            R.id.action_save -> {
               if( validateFields(mBinding.tilPhotoUrl,mBinding.tilPhone,mBinding.tilName)){

                   with(mStoreEntity){
                       name = mBinding.etName.text.toString().trim()
                       phone = mBinding.etPhone.text.toString().trim()
                       website = mBinding.etWebsite.text.toString().trim()
                       photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                   }
                   if(misEditMode) mEditStoreViewModel.updateStore(mStoreEntity)
                   else mEditStoreViewModel.saveStore(mStoreEntity)

               }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout):Boolean{
        var isValid = true

        for(textFiel in textFields){
            if (textFiel.editText?.text.toString().trim().isEmpty()){
                textFiel.error = getString(R.string.helper_required)
                isValid = false
            }else textFiel.error = null
        }

        if(!isValid) Snackbar.make(mBinding.root, R.string.edit_store_message_valid, Snackbar.LENGTH_SHORT).show()

        return isValid
    }

    private fun validateFields(): Boolean {
        var isValid = true

        if(mBinding.etPhotoUrl.text.toString().isEmpty()){
            mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
            mBinding.etPhotoUrl.requestFocus()
            isValid = false
        }
        if(mBinding.etPhone.text.toString().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.etPhone.requestFocus()
            isValid = false
        }
        if(mBinding.etName.text.toString().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.etName.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun hideKeyboard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken,0)
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mEditStoreViewModel.setShowFab(true)
        mEditStoreViewModel.setResult(Any())

        setHasOptionsMenu(false)
        super.onDestroy()
    }

  
}