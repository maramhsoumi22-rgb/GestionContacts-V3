package com.example.contactsapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

class ListeContactsFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var adapter: ContactAdapter
    private lateinit var editTextRecherche: TextInputEditText
    private lateinit var recyclerViewContacts: RecyclerView
    private lateinit var textViewEmpty: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_liste_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextRecherche = view.findViewById(R.id.editTextRecherche)
        recyclerViewContacts = view.findViewById(R.id.recyclerViewContacts)
        textViewEmpty = view.findViewById(R.id.textViewEmpty)

        // Configuration de la RecyclerView
        adapter = ContactAdapter(emptyList()) { contactSelectionne ->
            // Ouverture du fragment Détail avec passage de l'ID via Bundle
            val detailFragment = DetailContactFragment.newInstance(contactSelectionne.id)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack("liste_to_detail")
                .commit()
        }

        recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewContacts.adapter = adapter

        // Observation des données du ViewModel
        viewModel.contacts.observe(viewLifecycleOwner) { contactsList ->
            adapter.soumettreListe(contactsList)
            if (contactsList.isEmpty()) {
                textViewEmpty.visibility = View.VISIBLE
            } else {
                textViewEmpty.visibility = View.GONE
            }
        }

        // Filtrage en direct au fil de la saisie (sans bouton de validation)
        editTextRecherche.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filtrerContacts(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        // Rafraîchir les contacts au retour sur la liste
        viewModel.chargerContacts()
    }
}
