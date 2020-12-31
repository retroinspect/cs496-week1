package com.example.sample.ui.home

import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    val selection = null
    val selectionArgs = null
    val sortOrder = null
    val projections = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)
        val phones : RecyclerView = root.findViewById(R.id.phone_list)
        val adapter = PhoneAdapter()
        adapter.data = getPhoneNumbers()
        phones.adapter = adapter
        phones.layoutManager = LinearLayoutManager(context)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        //val phones_adapter = phones.adapter as PhoneAdapter
        //Log.i("HomeFragment", phones_adapter.data.toString())
        return root
    }

    private fun getPhoneNumbers() : ArrayList<PhoneModel> {
        var list : ArrayList<PhoneModel> = ArrayList<PhoneModel>()
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI //전화번호 URI
        //테이블에서 주소록 데이터 쿼리
        val cursorOrNull = context?.contentResolver?.query(phoneUri, projections,selection,selectionArgs, sortOrder)
        if (cursorOrNull != null) {
            val cursor = cursorOrNull
            val idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val id = cursor.getString(idColumn)
                val name = cursor.getString(nameColumn)
                val number = cursor.getString(numberColumn)
                val phoneModel = PhoneModel(name, number)
                list.add(phoneModel)
            }
            cursor.close()
        }
        return list
    }
}