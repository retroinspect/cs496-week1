package com.example.sample.ui.home

import android.app.Application
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.io.InputStream

class HomeViewModel(
    application : Application
) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    var allNumbers : MutableLiveData<ArrayList<PhoneModel>> = MutableLiveData()

    fun getPhoneNumbers(sort:String, searchName:String?) : ArrayList<PhoneModel> {
        var list : ArrayList<PhoneModel> = ArrayList<PhoneModel>()
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI //전화번호 URI
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
            val idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val id = cursor.getString(idColumn)
                val name = cursor.getString(nameColumn)
                var number = cursor.getString(numberColumn)
                val photo = getPhoto(id)
                number = featPhoneNumber(number)
                val phoneModel = PhoneModel(name, number, photo)
                list.add(phoneModel)
            }
            cursor.close()
        }
        return list
    }

    fun getPhoto(id : String) : Bitmap? {
        var uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
        var inputStream: InputStream? = ContactsContract.Contacts.openContactPhotoInputStream(context?.contentResolver, uri)
        var bitmapFactory: Bitmap? = BitmapFactory.decodeStream(inputStream)
        return bitmapFactory
    }

    fun featPhoneNumber(number : String) : String {
        var phone : String = number
        if (number.length == 11) {
            phone = number.substring(0,3) + "-" + number.substring(3,7) + "-" + number.substring(7,11)
        }
        else if (number.length == 10) {
            phone = number.substring(0,3) + "-" + number.substring(3,6) + "-" + number.substring(6,10)
        }
        return phone
    }

    fun setList(sortText : String, searchText : String) {
        allNumbers.setValue(getPhoneNumbers(sortText, searchText))
    }
}