package com.example.contactsapp

import java.io.Serializable

/**
 * Modèle de données représentant un contact
 */
data class Contact(
    val id: Long,
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String? = null,
    var photoUri: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
) : Serializable
