package com.example.sample.ui.home

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sample.R

data class Phone (val id:String?, val name:String?, val phone:String?)

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    var list = mutableListOf<Phone>()
    var sortText = "asc"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        setList()
        return root
    }

    fun setList() {
        list.addAll(getPhoneNumbers())
    }

    fun getPhoneNumbers() : List<Phone> {
        val list = mutableListOf<Phone>()
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI //전화번호 URI
        //전화번호에서 가져올 컬럼 정의
        val projections = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
        //정렬 쿼리 사용
        val optionSort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " $sortText"
        //테이블에서 주소록 데이터 쿼리
        val cursor = context?.getContentResolver()?.query(phoneUri, projections,null,null, optionSort)

        while(cursor?.moveToNext()?:false) {
            val id = cursor?.getString(0)
            val name = cursor?.getString(1)
            val number = cursor?.getString(2)
            val phone = Phone(id, name, number)
            list.add(phone)
        }
        return list
    }
}