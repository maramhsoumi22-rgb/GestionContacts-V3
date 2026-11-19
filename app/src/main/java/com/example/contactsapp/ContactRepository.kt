package com.example.contactsapp

/**
 * Dépôt en mémoire pour la collection de contacts (TP V3)
 */
object ContactRepository {

    private val contacts = mutableListOf(
        Contact(
            id = 1L,
            nom = "Ben Ali",
            prenom = "Youssef",
            telephone = "+216 20 123 456",
            email = "youssef.benali@example.com",
            photoUri = null,
            latitude = 36.8065,   // Tunis
            longitude = 10.1815
        ),
        Contact(
            id = 2L,
            nom = "Dupont",
            prenom = "Claire",
            telephone = "+33 6 12 34 56 78",
            email = "claire.dupont@example.com",
            photoUri = null,
            latitude = 48.8566,   // Paris
            longitude = 2.3522
        ),
        Contact(
            id = 3L,
            nom = "Trabelsi",
            prenom = "Amira",
            telephone = "+216 98 765 432",
            email = "amira.trabelsi@example.com",
            photoUri = null,
            latitude = 35.8256,   // Sousse
            longitude = 10.6084
        ),
        Contact(
            id = 4L,
            nom = "Martin",
            prenom = "Lucas",
            telephone = "+33 7 98 76 54 32",
            email = "lucas.martin@example.com",
            photoUri = null,
            latitude = null,      // Contact sans coordonnées GPS pour test
            longitude = null
        ),
        Contact(
            id = 5L,
            nom = "Khemir",
            prenom = "Sami",
            telephone = "+216 55 443 322",
            email = "sami.khemir@example.com",
            photoUri = null,
            latitude = null,      // Contact sans coordonnées GPS pour test
            longitude = null
        )
    )

    fun getContacts(): List<Contact> = contacts.toList()

    fun getContactById(id: Long): Contact? = contacts.find { it.id == id }

    fun updateContact(contactModifie: Contact) {
        val index = contacts.indexOfFirst { it.id == contactModifie.id }
        if (index != -1) {
            contacts[index] = contactModifie
        }
    }
}
