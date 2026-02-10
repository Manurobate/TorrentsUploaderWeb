# Torrents Uploader Web

## 🇫🇷 Français

### Présentation
Torrents Uploader Web est une application Spring Boot avec interface Web (Thymeleaf) qui permet d’envoyer un ou plusieurs fichiers `.torrent` vers un serveur **FTPS**. L’objectif est d’alimenter automatiquement un dossier `watch` surveillé par votre client torrent/NAS afin de démarrer les téléchargements sans manipulation manuelle sur le serveur.

### Fonctionnalités
- Interface Web simple accessible depuis un navigateur.
- Sélection multiple de fichiers `.torrent`.
- Chargement dynamique des sous-dossiers disponibles dans le répertoire `watch` distant.
- Validation du format des fichiers avant envoi (`.torrent` uniquement).
- Messages de succès/erreur affichés dans l’interface.

### Stack technique
- Java 21
- Spring Boot 3.3
- Spring MVC + Thymeleaf
- Apache Commons Net (`FTPSClient`)
- Maven

### Prérequis
- Java 21 installé
- Maven (ou usage du wrapper `./mvnw`)
- Un serveur FTPS accessible (hôte, port, utilisateur, mot de passe)

### Configuration
Configurez les propriétés dans `src/main/resources/application.properties` (ou via variables d’environnement / profil externe) :

```properties
server.port=9999
ftp.host=...
ftp.port=...
ftp.user=...
ftp.password=...
ftp.watchDirectory=watch
```

> `ftp.watchDirectory` doit pointer vers le dossier parent qui contient vos sous-dossiers de destination (par exemple `watch`).

### Lancer l’application en local
```bash
./mvnw spring-boot:run
```

Puis ouvrez :
- `http://localhost:9999`

### Build du JAR
```bash
./mvnw clean package
```

Le JAR est généré dans `target/`.

### Exécution avec Docker
Construire l’image :
```bash
docker build -t torrents-uploader-web .
```

Lancer le conteneur :
```bash
docker run --rm -p 9999:9999 \
  -e FTP_HOST="votre-hote" \
  -e FTP_PORT="21" \
  -e FTP_USER="votre-user" \
  -e FTP_PASSWORD="votre-password" \
  -e FTP_WATCH_DIRECTORY="watch" \
  torrents-uploader-web
```

### Tests
```bash
./mvnw test
```

Le projet inclut des tests Spring Boot et des tests FTPS avec un serveur de test embarqué.

---

## 🇬🇧 English

### Overview
Torrents Uploader Web is a Spring Boot web application (Thymeleaf UI) that uploads one or multiple `.torrent` files to an **FTPS** server. The main goal is to feed a remote `watch` folder monitored by your torrent client/NAS, so downloads can start automatically.

### Features
- Lightweight browser-based UI.
- Multi-file `.torrent` upload.
- Dynamic listing of destination subfolders from the remote watch directory.
- File-format validation before upload (`.torrent` only).
- Success/error feedback messages in the UI.

### Tech stack
- Java 21
- Spring Boot 3.3
- Spring MVC + Thymeleaf
- Apache Commons Net (`FTPSClient`)
- Maven

### Requirements
- Java 21
- Maven (or `./mvnw` wrapper)
- Reachable FTPS server (host, port, username, password)

### Configuration
Set properties in `src/main/resources/application.properties` (or override with environment variables / external profiles):

```properties
server.port=9999
ftp.host=...
ftp.port=...
ftp.user=...
ftp.password=...
ftp.watchDirectory=watch
```

> `ftp.watchDirectory` should be the parent folder containing the destination subfolders (for example `watch`).

### Run locally
```bash
./mvnw spring-boot:run
```

Then open:
- `http://localhost:9999`

### Build JAR
```bash
./mvnw clean package
```

The JAR is created in `target/`.

### Run with Docker
Build image:
```bash
docker build -t torrents-uploader-web .
```

Run container:
```bash
docker run --rm -p 9999:9999 \
  -e FTP_HOST="your-host" \
  -e FTP_PORT="21" \
  -e FTP_USER="your-user" \
  -e FTP_PASSWORD="your-password" \
  -e FTP_WATCH_DIRECTORY="watch" \
  torrents-uploader-web
```

### Tests
```bash
./mvnw test
```

The project includes Spring Boot tests and FTPS integration tests using an embedded test server.
