package com.lksh.dev.lkshassistant.utils

import com.lksh.dev.lkshassistant.R
import com.lksh.dev.lkshassistant.R.id.*


data class ContactsItem(val name: String,
                        val role: String,
                        val phone: String,
                        val imageSrc: Int,
                        val handlerView: Int) {

}

object ContactsData {
    var contacts = mutableListOf<ContactsItem>()

    init {
        contacts.add(ContactsItem("Станкевич Андрей Сергеевич", "Директор", "+7 (921) 903 4426", R.drawable.director, directrorContacts))
        contacts.add(ContactsItem("Кучеренко Демид Сергеевич", "Заместитель директора", "+7 (981) 830 1730", R.drawable.subdirector, subdirectrorContacts))
        contacts.add(ContactsItem("Саблина Маргарита Марковна", "Доктор", "+7 (916) 177 0055", R.drawable.doctor, doctorContacts))
    }
}