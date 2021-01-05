package com.example.sample.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.ui.dashboard.ImageAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    var searchText = ""
    var sortText = "asc"

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var root : View
    lateinit var phones : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_home, container, false)

        //setList()
        phones = root.findViewById(R.id.phone_list)
        val adapter = PhoneAdapter()
        homeViewModel.setList(sortText, searchText)

        //setSearchListener()
        val searchTap : EditText = root.findViewById(R.id.phone_search)
        searchTap.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                homeViewModel.setList(sortText, searchText)
            }
        })

        //setAddButtonListener()
        val addButton : FloatingActionButton = root.findViewById(R.id.phone_add_button)
        addButton.setOnClickListener {
            val addButtonIntent = Intent(context, CreateActivity::class.java)
            startActivityForResult(addButtonIntent,10001)
        }

        homeViewModel.allNumbers.observe(viewLifecycleOwner
        ) {
            it?.let {
                adapter.data = it
            }
        }
        phones.adapter = adapter
        phones.layoutManager = LinearLayoutManager(context)

        return root
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            val adapter = PhoneAdapter()
            adapter.data = homeViewModel.getPhoneNumbers(sortText, searchText)
            phones.adapter = adapter
            phones.layoutManager = LinearLayoutManager(context)
        }
    }
}