# Guide : Configuration Gmail pour l'envoi d'emails

Ce guide vous explique comment configurer Gmail pour permettre à votre application Spring Boot d'envoyer des emails automatiquement.

## ⚠️ Important

Gmail ne permet plus d'utiliser votre mot de passe normal pour les applications tierces. Vous devez créer un **mot de passe d'application** spécifique.

---

## 📋 Étapes détaillées

### Étape 1 : Accéder aux paramètres de sécurité Google

1. Ouvrez votre navigateur et allez sur [https://myaccount.google.com](https://myaccount.google.com)
2. Connectez-vous avec votre compte Gmail
3. Dans le menu de gauche, cliquez sur **"Sécurité"** (Security)

### Étape 2 : Activer la validation en 2 étapes

**⚠️ OBLIGATOIRE** : Vous devez d'abord activer la validation en 2 étapes pour pouvoir générer un mot de passe d'application.

1. Dans la section **"Connexion à Google"**, trouvez **"Validation en deux étapes"**
2. Cliquez sur **"Validation en deux étapes"**
3. Cliquez sur **"Commencer"**
4. Suivez les instructions pour configurer la validation en 2 étapes :
   - Entrez votre numéro de téléphone
   - Recevez un code par SMS
   - Entrez le code reçu
   - Confirmez l'activation

### Étape 3 : Générer un mot de passe d'application

Une fois la validation en 2 étapes activée :

1. Retournez à la page **"Sécurité"** de votre compte Google
2. Dans la section **"Connexion à Google"**, trouvez **"Mots de passe des applications"**
3. Cliquez sur **"Mots de passe des applications"**
4. Si c'est la première fois, Google vous demandera de vous reconnecter
5. Dans le champ **"Sélectionner l'application"**, choisissez **"Autre (nom personnalisé)"**
6. Entrez un nom descriptif, par exemple : **"Centre Formation App"**
7. Cliquez sur **"Générer"**
8. **⚠️ IMPORTANT** : Google affichera un mot de passe de 16 caractères (sans espaces)
   - **Copiez ce mot de passe immédiatement** - vous ne pourrez plus le voir après !
   - Exemple : `abcd efgh ijkl mnop` (sans les espaces : `abcdefghijklmnop`)

### Étape 4 : Configurer l'application Spring Boot

Maintenant, configurez votre application avec les identifiants Gmail :

#### Option A : Via `application.properties` (Développement)

Ouvrez le fichier `backend/src/main/resources/application-dev.properties` et ajoutez/modifiez :

```properties
# Email Configuration avec Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=abcdefghijklmnop
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

**Remplacez** :
- `votre-email@gmail.com` par votre adresse Gmail complète
- `abcdefghijklmnop` par le mot de passe d'application généré (16 caractères, sans espaces)

#### Option B : Via variables d'environnement (Recommandé pour la production)

Créez un fichier `.env` ou configurez les variables d'environnement :

**Windows (PowerShell)** :
```powershell
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="votre-email@gmail.com"
$env:MAIL_PASSWORD="abcdefghijklmnop"
$env:FRONTEND_URL="http://localhost:3000"
```

**Windows (CMD)** :
```cmd
set MAIL_HOST=smtp.gmail.com
set MAIL_PORT=587
set MAIL_USERNAME=votre-email@gmail.com
set MAIL_PASSWORD=abcdefghijklmnop
set FRONTEND_URL=http://localhost:3000
```

**Linux/Mac** :
```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=votre-email@gmail.com
export MAIL_PASSWORD=abcdefghijklmnop
export FRONTEND_URL=http://localhost:3000
```

### Étape 5 : Tester la configuration

1. Redémarrez votre serveur Spring Boot
2. Créez un nouvel étudiant ou formateur via l'interface admin
3. Vérifiez les logs de l'application :
   - Si vous voyez `✅ Email envoyé avec succès à: email@example.com`, c'est bon !
   - Si vous voyez une erreur, vérifiez les identifiants

---

## 🔍 Dépannage

### Erreur : "Username and Password not accepted"

**Solutions** :
1. Vérifiez que vous utilisez bien le **mot de passe d'application** (16 caractères) et non votre mot de passe Gmail normal
2. Vérifiez que la validation en 2 étapes est bien activée
3. Vérifiez que vous avez copié le mot de passe sans espaces

### Erreur : "Less secure app access"

Cette erreur n'apparaît plus avec les mots de passe d'application. Si vous la voyez, c'est que vous utilisez encore l'ancien système.

### Erreur : "Connection timeout"

**Solutions** :
1. Vérifiez votre connexion Internet
2. Vérifiez que le port 587 n'est pas bloqué par votre pare-feu
3. Essayez avec le port 465 (SSL) au lieu de 587 (TLS)

Pour utiliser le port 465 :
```properties
spring.mail.port=465
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.starttls.enable=false
```

### Les emails ne sont pas reçus

1. Vérifiez le dossier **Spam/Indésirables** de la boîte de réception
2. Vérifiez les logs de l'application pour voir si l'email a été envoyé
3. Vérifiez que l'adresse email de destination est correcte

---

## 📝 Exemple de configuration complète

### `application-dev.properties`

```properties
# Email Configuration avec Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=mon-email@gmail.com
spring.mail.password=mon-mot-de-passe-application-16-caracteres
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Frontend URL
app.frontend.url=http://localhost:3000
```

---

## 🔐 Sécurité

- **Ne commitez JAMAIS** votre mot de passe d'application dans Git
- Utilisez des variables d'environnement en production
- Si vous perdez votre mot de passe d'application, supprimez-le et créez-en un nouveau
- Vous pouvez avoir plusieurs mots de passe d'application pour différentes applications

---

## 📚 Ressources

- [Aide Google - Mots de passe des applications](https://support.google.com/accounts/answer/185833)
- [Configuration SMTP Gmail](https://support.google.com/a/answer/176600)

---

## ✅ Checklist de vérification

Avant de tester, assurez-vous que :

- [ ] La validation en 2 étapes est activée sur votre compte Google
- [ ] Vous avez généré un mot de passe d'application
- [ ] Vous avez copié le mot de passe (16 caractères, sans espaces)
- [ ] Vous avez configuré `application-dev.properties` ou les variables d'environnement
- [ ] Vous avez redémarré le serveur Spring Boot
- [ ] Vous avez testé en créant un nouvel utilisateur

---

**Besoin d'aide ?** Vérifiez les logs de l'application pour voir les messages d'erreur détaillés.
