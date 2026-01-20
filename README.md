# 🚀 Project Management Tool - Backend API

## 📋 Table des matières

1. [Prérequis](#-prérequis)
2. [Lancement rapide avec Docker](#-lancement-rapide-avec-docker)
3. [Tester l'API avec Bruno](#-tester-lapi-avec-bruno)
4. [Lancement en local (sans Docker)](#-lancement-en-local-sans-docker)
5. [Structure du projet](#-structure-du-projet)
6. [Tests unitaires et couverture de code](#-tests-unitaires-et-couverture-de-code)
7. [Endpoints de l'API](#-endpoints-de-lapi)

---

## 🔧 Prérequis

### Pour lancer avec Docker (recommandé)

- **Docker Desktop** : [Télécharger ici](https://www.docker.com/products/docker-desktop)
- **Bruno** (client API) : [Télécharger ici](https://www.usebruno.com/downloads)

### Pour lancer en local (sans Docker)

- **Java 21** : [Télécharger ici](https://adoptium.net/)
- **Maven 3.9+** : [Télécharger ici](https://maven.apache.org/download.cgi)
- **PostgreSQL 17** : [Télécharger ici](https://www.postgresql.org/download/)

---

## 🐳 Lancement rapide avec Docker

### Étape 1 : Cloner et accéder au projet

```bash
cd project-management-tool-back
```

### Étape 2 : Lancer tous les services avec Docker Compose

```bash
docker-compose up -d --build
```

Cette commande lance automatiquement :

- ✅ **Backend API** sur `http://localhost:8080`
- ✅ **PostgreSQL** sur le port `5432`
- ✅ **PgAdmin** sur `http://localhost:5050` (admin@example.com / admin)

### Étape 3 : Vérifier que tout fonctionne

```bash
# Voir les logs
docker-compose logs -f backend

# Vérifier l'état des containers
docker-compose ps
```

### Arrêter les services

```bash
# Arrêter les containers
docker-compose down

# Arrêter et supprimer les volumes (reset de la BDD)
docker-compose down -v
```

---

## 🚀 Premiers pas : Créer votre premier utilisateur

Une fois les services démarrés, vous devez créer un compte utilisateur pour utiliser l'application.

### Via Bruno (recommandé)

1. Ouvrez Bruno et chargez la collection (voir section suivante)
2. Allez dans **Auth > Register**
3. Modifiez le body JSON avec vos informations

### Via cURL / PowerShell

**Windows PowerShell :**

```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"VOTRE_PSEUDO","email":"VOTRE_EMAIL@exemple.com","password":"VOTRE_MOT_DE_PASSE"}'
```

**Linux / Mac / Git Bash :**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"VOTRE_PSEUDO","email":"VOTRE_EMAIL@exemple.com","password":"VOTRE_MOT_DE_PASSE"}'
```

### ⚠️ Règles de validation

| Champ        | Règles                                                                                                                                                                                                    |
| ------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **username** | Entre 3 et 50 caractères                                                                                                                                                                                  |
| **email**    | Format email valide (ex: `john@example.com`)                                                                                                                                                              |
| **password** | **Minimum 8 caractères** et doit contenir : <br>• Au moins 1 lettre majuscule (A-Z) <br>• Au moins 1 lettre minuscule (a-z) <br>• Au moins 1 chiffre (0-9) <br>• Au moins 1 caractère spécial (!@#$%^&\*) |

### ✅ Exemples de mots de passe valides

| Mot de passe  | Valide ? | Explication                                                 |
| ------------- | -------- | ----------------------------------------------------------- |
| `MonPass123!` | ✅ Oui   | Contient majuscule, minuscule, chiffre et caractère spécial |
| `Test@2024`   | ✅ Oui   | Contient majuscule, minuscule, chiffre et caractère spécial |
| `password`    | ❌ Non   | Manque majuscule, chiffre et caractère spécial              |
| `PASSWORD123` | ❌ Non   | Manque minuscule et caractère spécial                       |
| `Pass123`     | ❌ Non   | Manque caractère spécial                                    |

### Exemple complet

```json
{
  "username": "jean_dupont",
  "email": "jean.dupont@gmail.com",
  "password": "MonSuperPass123!"
}
```

**Réponse attendue (Status 201) :**

```json
{
  "id": 1,
  "username": "jean_dupont",
  "email": "jean.dupont@gmail.com",
  "createdAt": "2026-01-20T10:30:00"
}
```

---

## 🧪 Tester l'API avec Bruno

### Qu'est-ce que Bruno ?

[Bruno](https://www.usebruno.com/) est un client API open-source (alternative à Postman) qui permet de tester les endpoints de l'API facilement.

### Étape 1 : Installer Bruno

1. Téléchargez Bruno depuis : https://www.usebruno.com/downloads
2. Installez l'application sur votre ordinateur

### Étape 2 : Ouvrir la collection PMT-API

1. Ouvrez Bruno
2. Cliquez sur **"Open Collection"** (ou `Ctrl+O`)
3. Naviguez vers le dossier du projet :
   ```
   project-management-tool-back/bruno-collection/PMT-API
   ```
4. Sélectionnez ce dossier et ouvrez-le

### Étape 3 : Configurer l'environnement

1. Dans Bruno, cliquez sur le menu déroulant **"No Environment"** en haut à droite
2. Sélectionnez **"local"**
3. Cela configure automatiquement `baseUrl` sur `http://localhost:8080`

### Étape 4 : Tester les endpoints

La collection contient les requêtes suivantes organisées par catégorie :

#### 🔐 Auth (Authentification)

| Méthode | Endpoint             | Description                     |
| ------- | -------------------- | ------------------------------- |
| POST    | `/api/auth/register` | Créer un nouvel utilisateur     |
| POST    | `/api/auth/login`    | Se connecter                    |
| GET     | `/api/auth/{id}`     | Récupérer un utilisateur par ID |

#### 📁 Projects (Projets)

| Méthode | Endpoint                                     | Description                  |
| ------- | -------------------------------------------- | ---------------------------- |
| POST    | `/api/projects`                              | Créer un projet              |
| GET     | `/api/projects/{id}`                         | Récupérer un projet          |
| GET     | `/api/projects/{id}/members`                 | Lister les membres du projet |
| POST    | `/api/projects/{id}/members`                 | Inviter un membre            |
| PUT     | `/api/projects/{id}/members/{memberId}/role` | Modifier le rôle d'un membre |

#### ✅ Tasks (Tâches)

| Méthode | Endpoint                          | Description                   |
| ------- | --------------------------------- | ----------------------------- |
| POST    | `/api/projects/{projectId}/tasks` | Créer une tâche               |
| GET     | `/api/tasks/{id}`                 | Récupérer une tâche           |
| PUT     | `/api/tasks/{id}`                 | Modifier une tâche            |
| PUT     | `/api/tasks/{id}/assign`          | Assigner une tâche            |
| GET     | `/api/projects/{projectId}/tasks` | Lister les tâches d'un projet |
| GET     | `/api/tasks/{id}/history`         | Voir l'historique d'une tâche |

#### 🔔 Notifications

| Méthode | Endpoint                                     | Description                 |
| ------- | -------------------------------------------- | --------------------------- |
| GET     | `/api/users/{userId}/notifications`          | Récupérer les notifications |
| GET     | `/api/users/{userId}/notifications/unread`   | Notifications non lues      |
| PUT     | `/api/notifications/{id}/read`               | Marquer comme lue           |
| PUT     | `/api/users/{userId}/notifications/read-all` | Tout marquer comme lu       |

### Ordre recommandé pour tester

1. **Register** - Créer un utilisateur
2. **Login** - Se connecter (optionnel car pas de JWT)
3. **Create Project** - Créer un projet avec l'ID utilisateur
4. **Create Task** - Créer des tâches dans le projet
5. **Assign Task** - Assigner les tâches
6. **Get Notifications** - Vérifier les notifications créées

---

## 💻 Lancement en local (sans Docker)

### Étape 1 : Configurer PostgreSQL

Créez une base de données PostgreSQL :

```sql
CREATE DATABASE "project-management-tool";
```

### Étape 2 : Configurer les variables d'environnement

Créez un fichier `.env` ou configurez les variables :

```properties
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
```

### Étape 3 : Lancer l'application

```bash
mvn spring-boot:run
```

L'API sera disponible sur `http://localhost:8080`

---

## 📁 Structure du projet

```
project-management-tool-back/
├── src/
│   ├── main/java/com/iscod/project_management_tool_back/
│   │   ├── controller/     # Contrôleurs REST
│   │   ├── service/        # Logique métier
│   │   ├── repository/     # Accès aux données
│   │   ├── entity/         # Entités JPA
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── datamapper/     # Mappers DTO <-> Entity
│   │   ├── exception/      # Gestion des erreurs
│   │   └── config/         # Configuration Spring
│   └── test/               # Tests unitaires
├── bruno-collection/       # Collection Bruno pour tester l'API
├── docker-compose.yml      # Configuration Docker
├── Dockerfile             # Image Docker
└── pom.xml                # Dépendances Maven
```

---

## ✅ Tests unitaires et couverture de code

### Lancer les tests

```bash
# Lancer tous les tests
mvn test

# Générer le rapport de couverture JaCoCo
mvn test jacoco:report
```

### Voir le rapport de couverture

Le rapport HTML est généré dans :

```
target/site/jacoco/index.html
```

### Couverture actuelle : **93.55%** ✅

| Module      | Couverture |
| ----------- | ---------- |
| Controllers | 94-100%    |
| Services    | 75-100%    |
| Mappers     | 100%       |

### Classes exclues du coverage

- DTOs (data classes)
- Entities (persistence)
- Exceptions
- Configuration
- EmailServiceImpl (nécessite SMTP)

---

## 📝 Notes pour le correcteur

### Technologies utilisées

- **Spring Boot 3.4** - Framework backend
- **PostgreSQL 17** - Base de données
- **JUnit 5 + Mockito** - Tests unitaires
- **JaCoCo** - Couverture de code
- **Docker & Docker Compose** - Conteneurisation
- **Bruno** - Client API

### Points clés de l'implémentation

1. Architecture en couches (Controller → Service → Repository)
2. Gestion des erreurs centralisée avec `GlobalExceptionHandler`
3. Validation des données avec Jakarta Validation
4. Tests unitaires avec mocking (pas de base embarquée)
5. Configuration Docker multi-environnements

### Déploiement

L'application est également déployée sur Render (cloud) avec le profil `production`.

---

## 🆘 Dépannage

### Le backend ne démarre pas

```bash
# Vérifier les logs Docker
docker-compose logs backend

# Reconstruire les images
docker-compose up -d --build --force-recreate
```

### PostgreSQL refuse la connexion

```bash
# Vérifier que le container est démarré
docker-compose ps

# Attendre que PostgreSQL soit prêt (healthcheck)
docker-compose logs postgres
```

### Les tests Bruno échouent (404)

1. Vérifiez que l'environnement **"local"** est sélectionné
2. Vérifiez que le backend est démarré sur `http://localhost:8080`
3. Testez d'abord l'endpoint Register pour créer des données

---

**📧 Contact** : Pour toute question, n'hésitez pas à me contacter.
