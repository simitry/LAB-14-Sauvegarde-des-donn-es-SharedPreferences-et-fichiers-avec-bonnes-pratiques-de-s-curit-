# SecureStorageLabJava

## But du lab

Ce lab montre comment stocker des donnees Android en Java avec une separation claire entre donnees non sensibles et donnees sensibles.

## Fonctionnalites

- Preferences classiques avec `SharedPreferences` et `MODE_PRIVATE` pour le nom, la langue et le theme.
- Jeton stocke avec `EncryptedSharedPreferences` et une `MasterKey` basee sur Android Keystore.
- Fichiers internes UTF-8 pour `students.json` et `note.txt`.
- Cache temporaire pour `last_ui.txt`, avec purge manuelle.
- Export externe app-specifique avec `getExternalFilesDir(null)` pour des donnees non sensibles seulement.
- Interface unique avec sauvegarde, chargement, export, lecture et nettoyage.

## Architecture des packages

- `com.example.securestoragejava.ui` contient `MainActivity`.
- `com.example.securestoragejava.prefs` contient les preferences claires et chiffrees.
- `com.example.securestoragejava.files` contient les fichiers internes et le JSON.
- `com.example.securestoragejava.cache` contient la gestion du cache.
- `com.example.securestoragejava.external` contient le stockage externe app-specifique.
- `com.example.securestoragejava.model` contient le modele `Student`.

## Checklist securite

- Le token n'est jamais affiche en clair.
- Le token n'est jamais ecrit dans Logcat.
- Logcat affiche uniquement `tokenLength` pour le token.
- Les preferences non sensibles utilisent `MODE_PRIVATE`.
- Les fichiers internes utilisent `MODE_PRIVATE` et UTF-8.
- Le token utilise `EncryptedSharedPreferences`.
- L'export externe ne contient aucune donnee sensible.
- Aucune permission de stockage n'est ajoutee au manifeste.

## Tests de validation

1. Sauvegarder les preferences avec un nom, une langue, un theme et un token.
2. Charger les preferences et verifier que le champ token reste vide.
3. Verifier dans Logcat que seul `tokenLength` apparait.
4. Sauvegarder puis charger le JSON des etudiants.
5. Verifier la creation de `note.txt` en stockage interne.
6. Exporter puis lire `export.txt` en stockage externe app-specifique.
7. Nettoyer toutes les donnees et verifier que l'interface est reinitialisee.

## Depannage

- Si la dependance Security Crypto n'est pas reconnue, lancer une synchronisation Gradle manuelle dans Android Studio.
- Si l'export externe echoue, verifier que le stockage externe app-specifique est disponible sur l'emulateur ou l'appareil.
- Si `students.json` est absent ou corrompu, l'application retourne volontairement une liste vide.
- Si le cache disparait, c'est normal: Android peut supprimer `cacheDir` quand il manque d'espace.
