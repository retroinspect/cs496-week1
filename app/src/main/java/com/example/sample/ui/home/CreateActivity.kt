package com.example.sample.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import timber.log.Timber

class CreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_phone_number)

        var createButtonName = findViewById(R.id.create_phone_name) as EditText
        var createButtonNumber = findViewById(R.id.create_phone_number) as EditText
        var createPhoneButton = findViewById(R.id.create_phone_submit) as Button

        val createPhoneIntent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
        }
        createPhoneButton.setOnClickListener {
            var name: String = createButtonName.text.toString()
            var phoneNumber: String = createButtonNumber.text.toString()
            //Timber.i("Submit " + "$name" + " "+ "$phoneNumber")
            createPhoneIntent.apply {
                putExtra(ContactsContract.Intents.Insert.NAME, name)
                putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
            }
            startActivity(createPhoneIntent)
        }

    }
}
