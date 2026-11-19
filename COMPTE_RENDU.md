# Compte Rendu - TP Android Contacts V3

**Étudiant** : Maram Hsoumi  
**Matière** : Développement Mobile Android Kotlin  
**Projet** : TP Android — Gestion des contacts (Version 3)  

---

## 1. Synthèse de l'architecture
L'application adopte l'architecture recommandée par Google :
- **Activity unique** (`MainActivity`) qui initialise le conteneur `FragmentContainerView`.
- **Fragments indépendants** reliés par le `FragmentManager` avec gestion de l'historique via `addToBackStack()`.
- **ViewModel partagé (`ContactViewModel`)** associé au cycle de vie de l'Activity permettant une communication fluide et réactive entre la liste et le détail.

---

## 2. Intégration de Google Maps et Sécurité
L'intégration du SDK Google Maps a respecté les directives de sécurité :
- Déclaration de la dépendance `com.google.android.gms:play-services-maps`.
- Fichier `secrets.properties` et `local.properties` ignorés par Git via `.gitignore`.
- Injection dynamique de la variable `${MAPS_API_KEY}` dans le `AndroidManifest.xml` via `manifestPlaceholders`.
- Vérification rigoureuse des plages géographiques : Latitude $\in [-90, 90]$ et Longitude $\in [-180, 180]$.

---

## 3. Réponses aux questions théoriques
Toutes les 6 questions de compréhension (Différence Activity/Fragment, Rôle du ViewHolder, Prévention des erreurs d'indice au filtrage, Passage de l'ID vs position, Protection de clé API) sont intégralement détaillées dans le fichier `README.md`.

---

## 4. Difficultés rencontrées et solutions
1. **Intents implicites sous Android 11+ (API 30+)** :  
   Les Intent `ACTION_DIAL` et `ACTION_SENDTO` nécessitent désormais la balise `<queries>` dans le `AndroidManifest.xml` pour que le système autorise l'interrogation des applications téléphoniques et de messagerie installées.
2. **Empilement de fragments lors de la rotation d'écran** :  
   Pour éviter d'ajouter un fragment `ListeContactsFragment` à chaque rotation, la transaction initiale n'est exécutée que si `savedInstanceState == null`.
