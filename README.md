# MDD — Mon Monde de Développeur

Réseau social dédié aux développeurs : publiez des articles techniques, abonnez-vous à des thèmes et échangez dans les commentaires.

---

## Table des matières

1. [Présentation](#1-présentation)
2. [Architecture](#2-architecture)
3. [Technologies](#3-technologies)
4. [Prérequis](#4-prérequis)
5. [Installation](#5-installation)
6. [Configuration](#6-configuration)
7. [Lancement](#7-lancement)
8. [API — Référence des endpoints](#8-api--référence-des-endpoints)
9. [Schémas de données](#9-schémas-de-données)
10. [Tests](#10-tests)
11. [Déploiement](#11-déploiement)
12. [Sécurité et confidentialité](#12-sécurité-et-confidentialité)
13. [FAQ utilisateur](#13-faq-utilisateur)
14. [Structure du projet](#14-structure-du-projet)

---

## 1. Présentation

MDD est une application web full-stack permettant à des développeurs de :

- **Publier** des articles techniques rattachés à un thème (Java, Angular, Python…)
- **S'abonner** aux thèmes qui les intéressent pour composer leur fil d'actualité personnalisé
- **Commenter** les articles de la communauté
- **Gérer** leur profil (nom d'utilisateur, email, mot de passe)

L'authentification est basée sur JWT. Toutes les routes (sauf inscription et connexion) sont protégées.

---

## 2. Architecture

Le dépôt est organisé en **monorepo** avec deux applications indépendantes :

```
mdd/
├── back/     → API REST Spring Boot (Java 21)
└── front/    → SPA Angular 20
```

Le frontend communique exclusivement avec le backend via HTTP REST. Il n'y a pas de partage de code entre les deux.

**Flux de requête :**

```
Navigateur → Angular SPA → [AuthInterceptor: Bearer token]
          → API REST Spring Boot → JwtFilter → Controller → Service → Repository → MySQL
```

---

## 3. Technologies

### Backend

| Technologie         | Version  | Rôle                                  |
|---------------------|----------|---------------------------------------|
| Java                | 21       | Langage                               |
| Spring Boot         | 3.5.8    | Framework principal                   |
| Spring Security     | (inclus) | Authentification / autorisation       |
| Spring Data JPA     | (inclus) | ORM et accès base de données          |
| JJWT                | 0.12.3   | Génération et validation des JWT      |
| MySQL               | 8+       | Base de données de production         |
| H2                  | (test)   | Base de données en mémoire pour tests |
| JaCoCo              | (inclus) | Couverture de code                    |
| Maven               | 3.9+     | Gestion de build et dépendances       |

### Frontend

| Technologie         | Version  | Rôle                                  |
|---------------------|----------|---------------------------------------|
| Angular             | 20       | Framework SPA                         |
| TypeScript          | 5.9      | Langage                               |
| RxJS                | 7.8      | Programmation réactive                |
| Angular Signals     | (inclus) | Gestion d'état réactive fine-grained  |
| Karma + Jasmine     | 6.4 / 4  | Tests unitaires frontend              |
| karma-coverage      | 2.2      | Couverture de code frontend           |

---

## 4. Prérequis

Assurez-vous d'avoir installé sur votre machine :

- **Java 21** — [Télécharger](https://adoptium.net/)
- **Node.js 20+** et **npm 10+** — [Télécharger](https://nodejs.org/)
- **MySQL 8+** — [Télécharger](https://dev.mysql.com/downloads/)
- **Git**

Vérifiez les versions :

```bash
java -version        # openjdk 21.x.x
node -v              # v20.x.x ou supérieur
npm -v               # 10.x.x ou supérieur
mysql --version      # mysql  Ver 8.x.x
```

---

## 5. Installation

### 5.1 Base de données

Connectez-vous à MySQL en tant qu'administrateur et exécutez :

```sql
CREATE DATABASE mdd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'oc_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON mdd.* TO 'oc_user'@'localhost';
FLUSH PRIVILEGES;
```

> **Note :** Le schéma et les données de démo (topics, articles, commentaires) sont créés automatiquement au premier démarrage du backend grâce à `spring.jpa.hibernate.ddl-auto=create` et `import.sql`.

### 5.2 Backend

```bash
cd back

# Rendre le wrapper Maven exécutable (Linux / macOS)
chmod +x mvnw

# Installer les dépendances et compiler
./mvnw clean install -DskipTests
```

### 5.3 Frontend

```bash
cd front

# Installer les dépendances
npm install
```

---

## 6. Configuration

### Backend — `back/src/main/resources/application.properties`

Toutes les valeurs sensibles sont externalisées via des variables d'environnement avec une valeur par défaut pour le développement local.

| Variable d'environnement | Valeur par défaut (dev)                                        | Description                    |
|--------------------------|----------------------------------------------------------------|--------------------------------|
| `DB_URL`                 | `jdbc:mysql://localhost:3306/mdd?serverTimezone=UTC`           | URL JDBC de la base de données |
| `DB_USERNAME`            | `oc_user`                                                      | Utilisateur MySQL              |
| `DB_PASSWORD`            | `password`                                                     | Mot de passe MySQL             |
| `JWT_SECRET`             | Clé Base64 embarquée (dev uniquement)                          | Secret de signature JWT        |

Pour un environnement réel, définissez ces variables avant de lancer l'application :

```bash
export DB_URL=jdbc:mysql://prod-host:3306/mdd?serverTimezone=UTC
export DB_USERNAME=prod_user
export DB_PASSWORD=mot_de_passe_securise
export JWT_SECRET=votre_secret_base64_256_bits_minimum
```

Autres paramètres notables :

```properties
server.port=9000                        # Port de l'API
jwt.expiration=86400                    # Durée de vie du token JWT (secondes) — 24h
spring.jpa.hibernate.ddl-auto=create   # create en dev | update ou validate en prod
spring.jpa.show-sql=false              # Ne pas exposer les requêtes SQL dans les logs
```

### Frontend — `front/src/environments/`

| Fichier                   | Usage              | `apiUrl`                      |
|---------------------------|--------------------|-------------------------------|
| `environment.ts`          | Développement      | `http://localhost:9000/api`   |
| `environment.prod.ts`     | Production (build) | À adapter à l'URL de prod     |

Pour adapter l'URL de l'API en production, modifiez `environment.prod.ts` avant de builder.

---

## 7. Lancement

### Backend

```bash
cd back
./mvnw spring-boot:run
```

L'API est disponible sur `http://localhost:9000`.

Au premier démarrage, Spring Boot crée automatiquement :
- Le schéma de base de données
- Les 10 thèmes de démo
- 8 articles et 7 commentaires d'exemple
- Un utilisateur de test : `user@mdd.com` / `password`

### Frontend

```bash
cd front
npm start
```

L'application est disponible sur `http://localhost:4200`.

---

## 8. API — Référence des endpoints

> Toutes les routes sauf `/api/auth/**` nécessitent un header `Authorization: Bearer <token>`.

### Authentification

#### `POST /api/auth/register` — Inscription

**Corps de la requête :**

```json
{
  "username": "alice",
  "email": "alice@exemple.fr",
  "password": "motdepasse8"
}
```

| Champ      | Type   | Contraintes                         |
|------------|--------|-------------------------------------|
| `username` | string | Obligatoire, 3–30 caractères        |
| `email`    | string | Obligatoire, format email valide    |
| `password` | string | Obligatoire, 8 caractères minimum   |

**Réponse `201 Created` :**

```json
{
  "token": "eyJhbGciOiJIUzI1...",
  "user": { "id": 1, "username": "alice", "email": "alice@exemple.fr" }
}
```

**Erreurs :**
- `400` — Champs invalides (validation)
- `409` — Email ou nom d'utilisateur déjà utilisé

---

#### `POST /api/auth/login` — Connexion

**Corps de la requête :**

```json
{
  "identifier": "alice@exemple.fr",
  "password": "motdepasse8"
}
```

> `identifier` accepte aussi bien l'email que le nom d'utilisateur.

**Réponse `200 OK` :**

```json
{
  "token": "eyJhbGciOiJIUzI1...",
  "user": { "id": 1, "username": "alice", "email": "alice@exemple.fr" }
}
```

**Erreurs :**
- `401` — Identifiants incorrects

---

### Utilisateur

#### `GET /api/users/me` — Profil de l'utilisateur connecté

**Réponse `200 OK` :**

```json
{ "id": 1, "username": "alice", "email": "alice@exemple.fr" }
```

---

#### `PUT /api/users/me` — Mise à jour du profil

**Corps de la requête** (tous les champs sont optionnels) :

```json
{
  "username": "alice2",
  "email": "alice2@exemple.fr",
  "password": "nouveaumotdepasse"
}
```

| Champ      | Contraintes                                      |
|------------|--------------------------------------------------|
| `username` | 3–30 caractères (si fourni)                      |
| `email`    | Format email valide (si fourni)                  |
| `password` | 8 caractères minimum (si fourni)                 |

**Réponse `200 OK` :** UserDto mis à jour

**Erreurs :**
- `409` — Email ou nom d'utilisateur déjà pris par un autre compte

---

### Thèmes (Topics)

#### `GET /api/topics` — Liste de tous les thèmes

**Réponse `200 OK` :**

```json
[
  {
    "id": 1,
    "name": "Java",
    "description": "Tout sur le langage Java...",
    "subscribed": true
  },
  {
    "id": 2,
    "name": "Spring Boot",
    "description": "Framework Java pour APIs REST...",
    "subscribed": false
  }
]
```

> `subscribed: true` indique que l'utilisateur connecté est abonné à ce thème.

---

#### `POST /api/topics/{id}/subscribe` — S'abonner à un thème

**Réponse `200 OK`** (corps vide)

**Erreurs :**
- `404` — Thème introuvable

---

#### `DELETE /api/topics/{id}/subscribe` — Se désabonner d'un thème

**Réponse `200 OK`** (corps vide)

**Erreurs :**
- `404` — Thème introuvable

---

### Articles (Posts)

#### `GET /api/posts/feed` — Fil d'actualité personnalisé

Retourne les articles des thèmes auxquels l'utilisateur est abonné, triés du plus récent au plus ancien.

**Réponse `200 OK` :**

```json
[
  {
    "id": 5,
    "title": "Les Signals en Angular",
    "content": "Les Signals introduits en Angular 16...",
    "author": "charlie",
    "topicId": 3,
    "topicName": "Angular",
    "createdAt": "2025-04-05T16:00:00"
  }
]
```

---

#### `GET /api/posts/{id}` — Détail d'un article

**Réponse `200 OK` :** PostDto

**Erreurs :**
- `404` — Article introuvable

---

#### `POST /api/posts` — Créer un article

**Corps de la requête :**

```json
{
  "topicId": 3,
  "title": "Mon article sur Angular",
  "content": "Contenu de l'article..."
}
```

| Champ     | Type   | Contraintes                     |
|-----------|--------|---------------------------------|
| `topicId` | number | Obligatoire, doit exister       |
| `title`   | string | Obligatoire, max 255 caractères |
| `content` | string | Obligatoire                     |

**Réponse `201 Created` :** PostDto

**Erreurs :**
- `404` — Thème introuvable

---

### Commentaires

#### `GET /api/posts/{postId}/comments` — Commentaires d'un article

**Réponse `200 OK` :**

```json
[
  {
    "id": 1,
    "content": "Excellent article !",
    "author": "bob",
    "postId": 5,
    "createdAt": "2025-04-06T09:00:00"
  }
]
```

---

#### `POST /api/posts/{postId}/comments` — Ajouter un commentaire

**Corps de la requête :**

```json
{ "content": "Très instructif, merci !" }
```

**Réponse `201 Created` :** CommentDto

**Erreurs :**
- `404` — Article introuvable

---

### Format des erreurs

Toutes les erreurs retournent un JSON uniforme :

```json
{ "erreur": "Message descriptif" }
```

Pour les erreurs de validation (`400`), les champs incorrects sont détaillés :

```json
{
  "erreurs": {
    "email": "Format d'email invalide",
    "password": "Le mot de passe doit contenir au moins 8 caractères"
  }
}
```

---

## 9. Schémas de données

### Modèle entité-relation (simplifié)

```
users ──────────────────────── user_subscriptions ──── topics
  │  (id, username, email,      (user_id, topic_id)      │  (id, name, description)
  │   password, created_at)                               │
  │                                                       │
  └─── posts ──────────────────────────────── ManyToOne (topic)
         │  (post_id, topic_id, title,
         │   content, author, created_at)
         │
         └─── comments
                (comment_id, post_id, content,
                 author, created_at)
```

### Table `users`

| Colonne      | Type         | Contraintes             |
|--------------|--------------|-------------------------|
| `id`         | BIGINT       | PK, AUTO_INCREMENT      |
| `username`   | VARCHAR(255) | NOT NULL, UNIQUE        |
| `email`      | VARCHAR(255) | NOT NULL, UNIQUE        |
| `password`   | VARCHAR(255) | NOT NULL (BCrypt hash)  |
| `created_at` | DATETIME     |                         |

### Table `topics`

| Colonne       | Type         | Contraintes        |
|---------------|--------------|--------------------|
| `topic_id`    | BIGINT       | PK, AUTO_INCREMENT |
| `name`        | VARCHAR(255) | NOT NULL           |
| `description` | TEXT         |                    |

### Table `posts`

| Colonne      | Type         | Contraintes             |
|--------------|--------------|-------------------------|
| `post_id`    | BIGINT       | PK, AUTO_INCREMENT      |
| `topic_id`   | BIGINT       | FK → topics.topic_id    |
| `title`      | VARCHAR(255) | NOT NULL                |
| `content`    | TEXT         | NOT NULL                |
| `author`     | VARCHAR(255) | NOT NULL (username)     |
| `created_at` | DATETIME     | NOT NULL                |

### Table `comments`

| Colonne      | Type         | Contraintes             |
|--------------|--------------|-------------------------|
| `comment_id` | BIGINT       | PK, AUTO_INCREMENT      |
| `post_id`    | BIGINT       | FK → posts.post_id      |
| `content`    | TEXT         | NOT NULL                |
| `author`     | VARCHAR(255) | NOT NULL (username)     |
| `created_at` | DATETIME     | NOT NULL                |

### Table `user_subscriptions`

| Colonne    | Type   | Contraintes               |
|------------|--------|---------------------------|
| `user_id`  | BIGINT | FK → users.id             |
| `topic_id` | BIGINT | FK → topics.topic_id      |

> **Remarque :** Le champ `author` dans `posts` et `comments` stocke le `username` de l'utilisateur au moment de la publication. Cette dénormalisation intentionnelle préserve l'historique si l'utilisateur change de nom.

---

## 10. Tests

### Backend — Tests unitaires et d'intégration

```bash
cd back

# Lancer tous les tests
./mvnw test

# Lancer les tests et générer le rapport de couverture JaCoCo
./mvnw verify
```

Le rapport HTML de couverture est généré dans :
`back/target/site/jacoco/index.html`

**Organisation des tests :**

| Package                | Type         | Description                                    |
|------------------------|--------------|------------------------------------------------|
| `service/`             | Unitaire     | JUnit 5 + Mockito, pattern AAA                 |
| `integration/`         | Intégration  | SpringBootTest, H2 en mémoire, flux complets   |

Les DTOs et modèles sont exclus de la couverture JaCoCo (POJO sans logique à tester).

### Frontend — Tests unitaires

```bash
cd front

# Lancer les tests en mode watch
npm test

# Générer le rapport de couverture (single run)
npm test -- --watch=false --code-coverage
```

Le rapport HTML est généré dans :
`front/coverage/mdd-client/index.html`

---

## 11. Déploiement

### Points de vigilance avant la mise en production

1. **DDL auto** — Changer `spring.jpa.hibernate.ddl-auto=create` en `update` ou `validate` pour ne pas écraser les données existantes.

2. **Variables d'environnement** — Ne jamais committer les valeurs de production. Définir `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` et `JWT_SECRET` via le gestionnaire de secrets de votre infrastructure.

3. **JWT Secret** — Utiliser une clé d'au moins 256 bits en Base64. Générer avec :
   ```bash
   openssl rand -base64 32
   ```

4. **CORS** — Mettre à jour les origines autorisées dans `SecurityConfig.corsConfigurationSource()` avec le domaine réel du frontend.

5. **URL API** — Mettre à jour `front/src/environments/environment.prod.ts` avec l'URL de production de l'API.

6. **Build de production frontend :**
   ```bash
   cd front
   npm run build
   # Les fichiers compilés sont dans front/dist/mdd-client/
   ```

7. **Logs** — En profil `prod` (`-Dspring.profiles.active=prod`), les logs sont écrits dans `logs/mdd-api.log` avec rotation quotidienne sur 30 jours.

---

## 12. Sécurité et confidentialité

### Authentification

- Les mots de passe sont hashés avec **BCrypt** (facteur de coût 10) — les mots de passe en clair ne sont jamais stockés ni loggués.
- Les tokens JWT sont signés avec HMAC-SHA256 et ont une durée de vie de **24 heures**.
- Le secret JWT n'apparaît pas dans les logs. Externalisez-le via la variable `JWT_SECRET` en production.
- Le token est stocké côté client dans `localStorage` et inclus dans chaque requête via un intercepteur HTTP.

### Protection des routes

- Toutes les routes API sauf `/api/auth/**` sont protégées par le filtre JWT (`JwtFilter`).
- Le frontend protège les routes privées via `authGuard`, qui vérifie à la fois la présence et l'expiration du token côté client.
- Les sessions sont **stateless** (pas de session serveur, pas de cookie de session).

### Données personnelles (RGPD)

- Les données collectées sont : nom d'utilisateur, adresse email, mot de passe hashé, date d'inscription.
- Aucune donnée n'est transmise à des tiers.
- L'utilisateur peut modifier son nom, son email et son mot de passe à tout moment depuis son profil.
- Les requêtes SQL ne sont pas exposées dans les logs de production (`show-sql=false`).
- Les logs de production sont au niveau `WARN` pour le framework, `INFO` pour le code applicatif — aucun identifiant personnel n'est loggué.

### Bonnes pratiques appliquées

- Validation des entrées côté serveur (annotations Jakarta Validation) et côté client (Angular Validators).
- CORS restreint à l'origine du frontend uniquement.
- CSRF désactivé (API stateless avec JWT — pas de cookie de session à protéger).
- Réponses d'erreur uniformes sans exposition de stack traces.

---

## 13. FAQ utilisateur

### Comment créer un compte ?

Cliquez sur **S'inscrire** depuis la page d'accueil. Remplissez votre nom d'utilisateur, votre email et un mot de passe d'au moins 8 caractères. Vous êtes automatiquement connecté après l'inscription.

### Comment me connecter ?

Cliquez sur **Se connecter**. Entrez votre **email ou votre nom d'utilisateur** avec votre mot de passe.

> Votre session dure 24 heures. Au-delà, reconnectez-vous.

### Comment accéder à mon fil d'actualité ?

Le fil d'actualité (**Feed**) affiche les articles des thèmes auxquels vous êtes abonné, du plus récent au plus ancien. Utilisez le bouton de tri pour inverser l'ordre.

> Si votre fil est vide, abonnez-vous à des thèmes depuis la page **Thèmes**.

### Comment m'abonner à un thème ?

Allez dans **Thèmes** (menu de navigation). Chaque carte affiche un bouton **S'abonner** / **Se désabonner**. Le changement est immédiat.

### Comment me désabonner d'un thème ?

Deux façons :
- Depuis la page **Thèmes** : cliquez sur **Se désabonner**.
- Depuis votre **Profil** : la section *Abonnements* liste vos thèmes actifs avec un bouton de désabonnement.

### Comment publier un article ?

Cliquez sur le bouton **+** dans la barre de navigation. Sélectionnez un thème, entrez un titre et rédigez votre article, puis cliquez sur **Publier**.

### Comment commenter un article ?

Ouvrez un article depuis le fil d'actualité. En bas de la page, saisissez votre commentaire dans la zone de texte et cliquez sur le bouton d'envoi (icône avion). Les commentaires sont affichés du plus ancien au plus récent.

### Comment modifier mon profil ?

Cliquez sur votre avatar ou sur **Profil** dans le menu. Vous pouvez modifier votre nom d'utilisateur, votre email et votre mot de passe. Laissez le champ **Mot de passe** vide pour ne pas le changer. Cliquez sur **Sauvegarder**.

### Comment me déconnecter ?

Cliquez sur **Déconnexion** dans le menu de navigation. Votre token est supprimé localement et vous êtes redirigé vers la page d'accueil.

---

## 14. Structure du projet

```
mdd/
│
├── back/                                    # API REST Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/openclassrooms/mddapi/
│   │   │   │   ├── config/
│   │   │   │   │   ├── DataInitializer.java     # Seed utilisateur de test au démarrage
│   │   │   │   │   └── SecurityConfig.java      # Spring Security, CORS, JWT filter chain
│   │   │   │   ├── controller/
│   │   │   │   │   ├── AuthController.java      # POST /api/auth/register|login
│   │   │   │   │   ├── PostController.java      # GET|POST /api/posts
│   │   │   │   │   ├── CommentController.java   # GET|POST /api/posts/{id}/comments
│   │   │   │   │   ├── TopicController.java     # GET /api/topics, subscribe/unsubscribe
│   │   │   │   │   ├── UserController.java      # GET|PUT /api/users/me
│   │   │   │   │   └── GlobalExceptionHandler.java  # Gestionnaire d'erreurs centralisé
│   │   │   │   ├── dto/                         # Objets de transfert (entrée/sortie API)
│   │   │   │   ├── exception/
│   │   │   │   │   └── ResourceNotFoundException.java  # Exception 404 dédiée
│   │   │   │   ├── model/                       # Entités JPA (User, Post, Comment, Topic)
│   │   │   │   ├── repository/                  # Interfaces Spring Data JPA
│   │   │   │   ├── security/
│   │   │   │   │   ├── JwtFilter.java           # Filtre d'authentification par token
│   │   │   │   │   ├── JwtUtils.java            # Génération / validation JWT
│   │   │   │   │   └── JwtAuthEntryPoint.java   # Réponse 401 personnalisée
│   │   │   │   └── service/
│   │   │   │       ├── I*Service.java           # Interfaces (contrats métier)
│   │   │   │       └── *Service.java            # Implémentations
│   │   │   └── resources/
│   │   │       ├── application.properties       # Configuration principale
│   │   │       ├── import.sql                   # Données de démo (topics, posts, comments)
│   │   │       └── logback-spring.xml           # Configuration des logs par profil
│   │   └── test/
│   │       ├── java/.../service/                # Tests unitaires (JUnit 5 + Mockito)
│   │       └── java/.../integration/            # Tests d'intégration (SpringBootTest + H2)
│   └── pom.xml
│
└── front/                                   # SPA Angular 20
    ├── src/
    │   ├── app/
    │   │   ├── core/
    │   │   │   ├── guards/
    │   │   │   │   └── auth.guard.ts        # Protection des routes privées
    │   │   │   ├── interceptors/
    │   │   │   │   └── auth.interceptor.ts  # Injection automatique du Bearer token
    │   │   │   └── services/               # AuthService, PostService, TopicService…
    │   │   ├── features/
    │   │   │   ├── auth/                   # Landing, Login, Register
    │   │   │   ├── feed/                   # Fil d'actualité
    │   │   │   ├── posts/                  # Détail article, Création
    │   │   │   ├── profile/                # Profil utilisateur
    │   │   │   └── topics/                 # Liste des thèmes
    │   │   └── shared/
    │   │       ├── components/             # Navbar, PostCard (réutilisables)
    │   │       └── models/                 # Interfaces TypeScript
    │   ├── environments/                   # Configuration dev / prod
    │   └── index.html                      # Point d'entrée HTML
    ├── angular.json
    ├── karma.conf.js                       # Configuration des tests + couverture
    └── package.json
```
