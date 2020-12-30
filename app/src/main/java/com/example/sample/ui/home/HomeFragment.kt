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
        getPhoneNumbers()
        //list.addAll(getPhoneNumbers())
    }

    fun getPhoneNumbers() {
        val list = mutableListOf<Phone>()
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI //전화번호 URI
        //전화번호에서 가져올 컬럼 정의
        val projections = arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
    }
}