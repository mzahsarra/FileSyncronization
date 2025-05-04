JSync - Outil de Synchronisation de Fichiers:
Prérequis
Java : JDK (testé avec OpenJDK 21).
Bibliothèque JSON : json-20231013.jar (téléchargeable sur Maven Central).

Utilisation:
Étape 1 : Créer les répertoires à synchroniser

JSync propose trois point d'entrées: new-profile, sync et synostat

Étape 2 : Créer un profil (new-profile)
Utilisez la classe NewProfile pour créer un profil de synchronisation. 
Configurez les Run Configurations dans votre IDE pour passer trois arguments :
<nomProfil> : Nom du profil (ex. : testProfile).
<cheminA> : Chemin du premier répertoire (ex. : /tmp/testA).
<cheminB> : Chemin du second répertoire (ex. : /tmp/testB).
Configuration dans l'IDE :
Allez dans Run > Edit Configurations.
Ajoutez une nouvelle configuration pour Application
Main class : fr.urouen.sync.cli.NewProfile
Program arguments : testProfile /tmp/testA /tmp/testB
Enregistrez et exécutez.
Sortie : Profil créé avec succès
Résultat : Crée un fichier testProfile.sync (format JSON)

Étape 3 : Synchroniser les fichiers
Utilisez la classe synmain pour synchroniser les fichiers entre pathA et PathB. 
Configurez les Run Configurations pour passer un argument : 
Nom du profil créé 
Main class fr.urouen.sync.cli.SyncMain
Enregistrez et exécutez.

Etape 4 : même configuration pour Synostat que SyncMain

JSync/
├── src/
│   └── fr/urouen/sync/
│       ├── cli/                
│       │   ├── NewProfile.java      
│       │   ├── SyncMain.java        
│       │   └── Synostat.java        
│       ├── exception/         
│       │   ├── FileAccessException.java       
│       │   ├── ProfileCorruptedException.java 
│       │   └── SyncException.java           
│       ├── model/            
│       │   └── SyncRegistry.java      
│       ├── profile/  
│       │   ├── ProfileManager.java      
│       │   ├── ProfileFactory.java      
│       │   ├── Profile.java
│       │   └── ProfileBuilder.java     
│       ├── sync/             
│       │   ├── Sync.java             
│       │   ├── SyncFacade.java       
│       │   ├── Observer.java 
│       │   ├── FileSystemElement.java         
│       │   ├── SyncObserver.java 
|       ├── filesystem/
│       │   ├── FileSystem.java       
│       │   ├── LocalFileSystem.java  
│       │   └── WebDavFileSystem.java 
│       └── ui/                
│           └── ConsoleUI.java 
└── pom.xml

new-profile : Créer un profil de synchronisation avec deux chemins de répertoires, stocké dans un fichier .sync .

sync : Synchroniser les fichiers entre deux répertoires, en gérant les copies, suppressions et conflits.

syncstat : Afficher les détails du profil et l'historique de synchronisation (timestamps des dernières modifications et résolutions de conflits).

Patrons de conception :
Singleton : ProfileManager garantit une instance unique pour gérer les profils.

Factory Method : ProfileFactory permet la création extensible de profils.

Builder : ProfileBuilder construit les profils de manière fluide.

Composite : FileSystemElement représente les fichiers uniformément.

Observer : SyncObserver notifie les actions de synchronisation.

Façade : SyncFacade simplifie l'accès au sous-système de synchronisation.
