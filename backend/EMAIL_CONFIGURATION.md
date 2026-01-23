# Configuration de l'envoi d'emails

## Fonctionnalité

Lorsqu'un administrateur crée un **étudiant** ou un **formateur**, un email automatique est envoyé à l'utilisateur contenant :
- Son **matricule** (login)
- Son **mot de passe** (égal au matricule en minuscules)
- Le **lien du site web** pour se connecter

## Configuration

### Mode Développement (sans serveur SMTP)

Par défaut, si aucun serveur SMTP n'est configuré, les emails seront seulement **loggés** dans la console. Cela permet de tester sans configuration.

### Mode Production (avec serveur SMTP)

Pour envoyer de vrais emails, configurez les propriétés suivantes dans `application.properties` ou via des variables d'environnement :

#### Exemple avec Gmail

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-application
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Important pour Gmail** : Vous devez utiliser un **mot de passe d'application** et non votre mot de passe Gmail normal.

📖 **Guide détaillé** : Consultez le fichier `GMAIL_SETUP_GUIDE.md` pour les étapes complètes avec captures d'écran.

**Résumé rapide** :
1. Allez dans votre compte Google → Sécurité
2. Activez la validation en 2 étapes (obligatoire)
3. Générez un mot de passe d'application (16 caractères)
4. Utilisez ce mot de passe dans la configuration

#### Exemple avec un autre serveur SMTP

```properties
spring.mail.host=smtp.votre-serveur.com
spring.mail.port=587
spring.mail.username=votre-email@votre-domaine.com
spring.mail.password=votre-mot-de-passe
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Configuration du Frontend URL

Pour que les liens dans les emails pointent vers le bon site :

```properties
app.frontend.url=http://localhost:3000  # Développement
app.frontend.url=https://votre-domaine.com  # Production
```

## Variables d'environnement

Vous pouvez aussi utiliser des variables d'environnement :

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=votre-email@gmail.com
export MAIL_PASSWORD=votre-mot-de-passe-application
export FRONTEND_URL=http://localhost:3000
```

## Test

1. Créez un nouvel étudiant ou formateur via l'interface admin
2. Vérifiez les logs pour voir l'email généré
3. Si SMTP est configuré, l'email sera envoyé
4. Sinon, l'email sera seulement loggé dans la console

## Contenu de l'email

L'email envoyé contient :
- Un message de bienvenue personnalisé
- Le matricule (login)
- Le mot de passe (égal au matricule)
- Le lien vers la page de connexion
- Un rappel de changer le mot de passe après la première connexion
