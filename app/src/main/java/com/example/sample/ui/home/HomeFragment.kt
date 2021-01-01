package com.example.sample.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    var searchText = ""
    var sortText = "asc"

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)

        //setList()
        val phones : RecyclerView = root.findViewById(R.id.phone_list)
        val adapter = PhoneAdapter()
        adapter.data = getPhoneNumbers(sortText, searchText)
        phones.adapter = adapter
        phones.layoutManager = LinearLayoutManager(context)

        //setSearchListener()
        val searchTap : EditText = root.findViewById(R.id.phone_search)
        searchTap.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                val newList = getPhoneNumbers(sortText, searchText)
                adapter.data.clear()
                adapter.data = newList
                phones.adapter = adapter
            }
        })

        //setAddButtonListener()
        val addButton : FloatingActionButton = root.findViewById(R.id.phone_add_button)
        addButton.setOnClickListener({
            val addButtonIntent = Intent(context, CreateActivity::class.java)
            startActivity(addButtonIntent)
        })

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        return root
    }

    private fun getPhoneNumbers(sort:String, searchName:String?) : ArrayList<PhoneModel> {
        var list : ArrayList<PhoneModel> = ArrayList<PhoneModel>()
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI //전화번호 URI
        val id = ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        val projections = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
        //조건 정의
        var wheneClause:String? = null
        var whereValues:Array<String>? = null
        //검색 내용 있을 경우 검색 사용
        if(searchName?.isNotEmpty() ?: false) {
            wheneClause = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like ?"
            whereValues = arrayOf("%$searchName%")
        }
        //정렬
        val optionSort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " $sort"
        //테이블에서 주소록 데이터 쿼리
        val cursorOrNull = context?.contentResolver?.query(phoneUri,projections,wheneClause,whereValues,optionSort)
        if (cursorOrNull != null) {
            val cursor = cursorOrNull
            val nameColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
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