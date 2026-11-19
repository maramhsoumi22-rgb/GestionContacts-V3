package com.example.contactsapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onContactClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewPhoto: ImageView = itemView.findViewById(R.id.imageViewContact)
        val textViewNomPrenom: TextView = itemView.findViewById(R.id.textViewNomPrenom)
        val textViewTelephone: TextView = itemView.findViewById(R.id.textViewTelephone)

        fun bind(contact: Contact) {
            val nomComplet = "${contact.nom.uppercase()} ${contact.prenom}"
            textViewNomPrenom.text = nomComplet
            textViewTelephone.text = contact.telephone

            if (!contact.photoUri.isNullOrEmpty()) {
                try {
                    imageViewPhoto.setImageURI(Uri.parse(contact.photoUri))
                } catch (e: Exception) {
                    imageViewPhoto.setImageResource(R.drawable.ic_avatar_placeholder)
                }
            } else {
                imageViewPhoto.setImageResource(R.drawable.ic_avatar_placeholder)
            }

            itemView.setOnClickListener {
                onContactClick(contact)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount(): Int = contacts.size

    fun soumettreListe(nouvelleListe: List<Contact>) {
        this.contacts = nouvelleListe
        notifyDataSetChanged()
    }
}
