package com.example.contactsapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText

class DetailContactFragment : Fragment() {

    private val viewModel: ContactViewModel by activityViewModels()
    private var contactId: Long = -1L
    private var contactCourant: Contact? = null

    // Vues
    private lateinit var imageViewPhoto: ImageView
    private lateinit var btnGalerie: Button
    private lateinit var btnCamera: Button
    private lateinit var btnAppeler: Button
    private lateinit var btnEmail: Button
    private lateinit var editTextNom: TextInputEditText
    private lateinit var editTextPrenom: TextInputEditText
    private lateinit var editTextTelephone: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextLatitude: TextInputEditText
    private lateinit var editTextLongitude: TextInputEditText
    private lateinit var btnVoirCarte: Button
    private lateinit var btnAnnuler: Button
    private lateinit var btnEnregistrer: Button

    // Photo URI temporaire ou sélectionnée
    private var photoSelectionneeUri: String? = null

    // ActivityResult pour la galerie
    private val selectPictureLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoSelectionneeUri = uri.toString()
            imageViewPhoto.setImageURI(uri)
        }
    }

    // ActivityResult pour la caméra
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            imageViewPhoto.setImageBitmap(bitmap)
            // Enregistrement temporaire de l'image bitmap
            val path = MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                bitmap,
                "Contact_${System.currentTimeMillis()}",
                null
            )
            photoSelectionneeUri = path
        }
    }

    companion object {
        private const val ARG_CONTACT_ID = "arg_contact_id"

        fun newInstance(contactId: Long): DetailContactFragment {
            val fragment = DetailContactFragment()
            val args = Bundle().apply {
                putLong(ARG_CONTACT_ID, contactId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactId = arguments?.getLong(ARG_CONTACT_ID) ?: -1L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Liaison des vues
        imageViewPhoto = view.findViewById(R.id.detailImageViewPhoto)
        btnGalerie = view.findViewById(R.id.btnGalerie)
        btnCamera = view.findViewById(R.id.btnCamera)
        btnAppeler = view.findViewById(R.id.btnAppeler)
        btnEmail = view.findViewById(R.id.btnEmail)
        editTextNom = view.findViewById(R.id.editTextNom)
        editTextPrenom = view.findViewById(R.id.editTextPrenom)
        editTextTelephone = view.findViewById(R.id.editTextTelephone)
        editTextEmail = view.findViewById(R.id.editTextEmail)
        editTextLatitude = view.findViewById(R.id.editTextLatitude)
        editTextLongitude = view.findViewById(R.id.editTextLongitude)
        btnVoirCarte = view.findViewById(R.id.btnVoirCarte)
        btnAnnuler = view.findViewById(R.id.btnAnnuler)
        btnEnregistrer = view.findViewById(R.id.btnEnregistrer)

        // Chargement du contact
        chargerDonneesContact()

        // Gestion de la photo
        btnGalerie.setOnClickListener {
            selectPictureLauncher.launch("image/*")
        }
        btnCamera.setOnClickListener {
            takePictureLauncher.launch(null)
        }

        // Actions d'appel et d'email par Intents implicites
        btnAppeler.setOnClickListener {
            val telephone = editTextTelephone.text?.toString()?.trim() ?: ""
            if (telephone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telephone"))
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), getString(R.string.erreur_aucune_app_appel), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.erreur_telephone_invalide), Toast.LENGTH_SHORT).show()
            }
        }

        btnEmail.setOnClickListener {
            val email = editTextEmail.text?.toString()?.trim() ?: ""
            if (email.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), getString(R.string.erreur_aucune_app_mail), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.champ_obligatoire), Toast.LENGTH_SHORT).show()
            }
        }

        // Clic sur "Voir sur la carte"
        btnVoirCarte.setOnClickListener {
            ouvrirCarte()
        }

        // Bouton Annuler
        btnAnnuler.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.action_annulee), Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        // Bouton Enregistrer
        btnEnregistrer.setOnClickListener {
            enregistrerModifications()
        }
    }

    private fun chargerDonneesContact() {
        contactCourant = viewModel.getContactById(contactId)
        contactCourant?.let { contact ->
            editTextNom.setText(contact.nom)
            editTextPrenom.setText(contact.prenom)
            editTextTelephone.setText(contact.telephone)
            editTextEmail.setText(contact.email ?: "")
            editTextLatitude.setText(contact.latitude?.toString() ?: "")
            editTextLongitude.setText(contact.longitude?.toString() ?: "")
            photoSelectionneeUri = contact.photoUri

            if (!contact.photoUri.isNullOrEmpty()) {
                try {
                    imageViewPhoto.setImageURI(Uri.parse(contact.photoUri))
                } catch (e: Exception) {
                    imageViewPhoto.setImageResource(R.drawable.ic_avatar_placeholder)
                }
            }
        }
    }

    private fun ouvrirCarte() {
        val latStr = editTextLatitude.text?.toString()?.trim() ?: ""
        val lngStr = editTextLongitude.text?.toString()?.trim() ?: ""

        if (latStr.isEmpty() || lngStr.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.erreur_coordonnees_absentes), Toast.LENGTH_LONG).show()
            return
        }

        val lat = latStr.toDoubleOrNull()
        val lng = lngStr.toDoubleOrNull()

        if (lat == null || lng == null || lat < -90.0 || lat > 90.0 || lng < -180.0 || lng > 180.0) {
            Toast.makeText(requireContext(), getString(R.string.erreur_coordonnees_invalides), Toast.LENGTH_LONG).show()
            return
        }

        val nom = editTextNom.text?.toString() ?: "Contact"
        val prenom = editTextPrenom.text?.toString() ?: ""
        val carteFragment = CarteContactFragment.newInstance("$prenom $nom", lat, lng)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, carteFragment)
            .addToBackStack("detail_to_map")
            .commit()
    }

    private fun enregistrerModifications() {
        val nom = editTextNom.text?.toString()?.trim() ?: ""
        val prenom = editTextPrenom.text?.toString()?.trim() ?: ""
        val telephone = editTextTelephone.text?.toString()?.trim() ?: ""
        val email = editTextEmail.text?.toString()?.trim()
        val latStr = editTextLatitude.text?.toString()?.trim() ?: ""
        val lngStr = editTextLongitude.text?.toString()?.trim() ?: ""

        // Validation des champs obligatoires
        var valide = true
        if (nom.isEmpty()) {
            editTextNom.error = getString(R.string.champ_obligatoire)
            valide = false
        }
        if (prenom.isEmpty()) {
            editTextPrenom.error = getString(R.string.champ_obligatoire)
            valide = false
        }
        if (telephone.isEmpty()) {
            editTextTelephone.error = getString(R.string.champ_obligatoire)
            valide = false
        }

        // Validation des coordonnées GPS si renseignées
        var lat: Double? = null
        var lng: Double? = null

        if (latStr.isNotEmpty() || lngStr.isNotEmpty()) {
            lat = latStr.toDoubleOrNull()
            lng = lngStr.toDoubleOrNull()
            if (lat == null || lng == null || lat < -90.0 || lat > 90.0 || lng < -180.0 || lng > 180.0) {
                Toast.makeText(requireContext(), getString(R.string.erreur_coordonnees_invalides), Toast.LENGTH_LONG).show()
                valide = false
            }
        }

        if (!valide) return

        val contactMisAJour = Contact(
            id = contactId,
            nom = nom,
            prenom = prenom,
            telephone = telephone,
            email = if (email.isNullOrEmpty()) null else email,
            photoUri = photoSelectionneeUri,
            latitude = lat,
            longitude = lng
        )

        viewModel.sauvegarderContact(contactMisAJour)
        Toast.makeText(requireContext(), getString(R.string.succes_contact_enregistre), Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }
}
