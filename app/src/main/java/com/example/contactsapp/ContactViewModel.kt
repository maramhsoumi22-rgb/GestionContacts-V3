package com.example.contactsapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactViewModel : ViewModel() {

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    private var listeOriginale: List<Contact> = emptyList()
    private var dernierTexteFiltre: String = ""

    init {
        chargerContacts()
    }

    fun chargerContacts() {
        listeOriginale = ContactRepository.getContacts()
        appliquerFiltre(dernierTexteFiltre)
    }

    fun filtrerContacts(query: String) {
        dernierTexteFiltre = query
        appliquerFiltre(query)
    }

    private fun appliquerFiltre(query: String) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) {
            _contacts.value = listeOriginale
        } else {
            _contacts.value = listeOriginale.filter { c ->
                c.nom.lowercase().contains(q) ||
                c.prenom.lowercase().contains(q) ||
                c.telephone.lowercase().contains(q)
            }
        }
    }

    fun sauvegarderContact(contact: Contact) {
        ContactRepository.updateContact(contact)
        chargerContacts()
    }

    fun getContactById(id: Long): Contact? {
        return ContactRepository.getContactById(id)
    }
}
