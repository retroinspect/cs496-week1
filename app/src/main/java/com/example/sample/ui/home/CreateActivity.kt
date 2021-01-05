package com.example.sample.ui.home

import android.app.Activity
import android.content.ContentProviderOperation
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sample.R
import timber.log.Timber
import kotlin.collections.ArrayList

class CreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_phone_number)

        var createButtonName = findViewById(R.id.create_phone_name) as EditText
        var createButtonNumber = findViewById(R.id.create_phone_number) as EditText
        var createPhoneButton = findViewById(R.id.create_phone_submit) as Button

        createPhoneButton.setOnClickListener {
            var name: String = createButtonName.text.toString()
            var phoneNumber: String = createButtonNumber.text.toString()
            if (name == "" || phoneNumber == "") {
                Toast.makeText(this, "이름과 전화번호는 공백일 수 없습니다.", Toast.LENGTH_LONG).show()
                setResult(Activity.RESULT_CANCELED)
                finish()
                onResume()
            }
            else {
                var ops: ArrayList<ContentProviderOperation> = ArrayList<ContentProviderOperation>()
                var op: ContentProviderOperation.Builder =
                    ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                ops.add(op.build())

                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                ops.add(op.build())

                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "LABEL?")
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                ops.add(op.build())
                this.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops)
                Toast.makeText(this, "저장되었습니다", Toast.LENGTH_LONG).show()
                setResult(Activity.RESULT_OK)
                finish()
                onResume()
            }
        }
    }
}
