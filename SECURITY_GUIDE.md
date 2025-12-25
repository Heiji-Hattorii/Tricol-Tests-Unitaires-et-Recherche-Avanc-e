# 🔐 Guide de Sécurité - API Tricol

## 📋 Vue d'ensemble

L'API Tricol est maintenant sécurisée avec JWT (JSON Web Tokens) et un système de permissions granulaires basé sur les rôles.

## 🚀 Démarrage rapide

### Utilisateur Admin par défaut
- **Email**: `admin@tricol.com`
- **Mot de passe**: `secret` (BCrypt: `$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.`)

### Endpoints d'authentification

#### 1. Inscription
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "motdepasse123"
}
```

#### 2. Connexion
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@tricol.com",
  "password": "secret"
}
```

#### 3. Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "votre_refresh_token"
}
```

#### 4. Déconnexion
```http
POST /api/auth/logout
Authorization: Bearer votre_access_token
```

## 👥 Rôles et Permissions

### Rôles disponibles
1. **ADMIN** - Accès complet à toutes les fonctionnalités
2. **RESPONSABLE_ACHATS** - Gestion des achats et approvisionnements
3. **MAGASINIER** - Gestion du stock et des sorties
4. **CHEF_ATELIER** - Consultation et création de bons de sortie

### Matrice des permissions

| Fonctionnalité | ADMIN | RESP_ACHATS | MAGASINIER | CHEF_ATELIER |
|----------------|-------|-------------|------------|--------------|
| **Fournisseurs** |
| Créer/Modifier/Supprimer | ✅ | ✅ | ❌ | ❌ |
| Consulter | ✅ | ✅ | ✅ | ❌ |
| **Produits** |
| Créer/Modifier/Supprimer | ✅ | ✅ | ❌ | ❌ |
| Consulter | ✅ | ✅ | ✅ | ✅ |
| Configurer seuils d'alerte | ✅ | ✅ | ❌ | ❌ |
| **Commandes Fournisseurs** |
| Créer/Modifier | ✅ | ✅ | ❌ | ❌ |
| Valider | ✅ | ✅ | ❌ | ❌ |
| Annuler | ✅ | ✅ | ❌ | ❌ |
| Réceptionner | ✅ | ❌ | ✅ | ❌ |
| Consulter | ✅ | ✅ | ✅ | ❌ |
| **Stock & Lots** |
| Consulter stock/lots | ✅ | ✅ | ✅ | ✅ |
| Voir valorisation FIFO | ✅ | ✅ | ✅ | ❌ |
| Consulter historique mouvements | ✅ | ✅ | ✅ | ✅ |
| **Bons de Sortie** |
| Créer (brouillon) | ✅ | ❌ | ✅ | ✅ |
| Valider | ✅ | ❌ | ✅ | ❌ |
| Annuler | ✅ | ❌ | ✅ | ❌ |
| Consulter | ✅ | ✅ | ✅ | ✅ |

## 🔧 Gestion des utilisateurs (Admin uniquement)

### Assigner un rôle à un utilisateur
```http
PUT /api/admin/users/{userId}/role/{roleId}
Authorization: Bearer admin_token
```

### Modifier les permissions d'un utilisateur
```http
POST /api/admin/users/permissions
Authorization: Bearer admin_token
Content-Type: application/json

{
  "userId": 1,
  "permissionId": 2,
  "active": true
}
```

### Supprimer une permission d'un utilisateur
```http
DELETE /api/admin/users/{userId}/permissions/{permissionId}
Authorization: Bearer admin_token
```

## 🔍 Système d'audit

Toutes les actions sensibles sont automatiquement tracées :
- Connexions/déconnexions
- Modifications de permissions
- Actions sur les entités métier
- Changements de statut

## 🛡️ Utilisation des tokens

### Headers requis
```http
Authorization: Bearer votre_access_token
Content-Type: application/json
```

### Durée de vie des tokens
- **Access Token**: 24 heures
- **Refresh Token**: 7 jours

## 📝 Exemples d'utilisation

### Créer un fournisseur (ADMIN ou RESPONSABLE_ACHATS)
```http
POST /api/fournisseurs
Authorization: Bearer token_avec_permission_FOURNISSEUR_CREATE
Content-Type: application/json

{
  "nom": "Fournisseur Test",
  "email": "test@fournisseur.com",
  "telephone": "0123456789",
  "ICE": "123456789"
}
```

### Consulter le stock (Tous sauf CHEF_ATELIER pour valorisation)
```http
GET /api/stock
Authorization: Bearer token_avec_permission_STOCK_READ
```

### Créer un bon de sortie (MAGASINIER ou CHEF_ATELIER)
```http
POST /api/bonsSortie
Authorization: Bearer token_avec_permission_BON_SORTIE_CREATE
Content-Type: application/json

{
  "motif": "PRODUCTION",
  "items": [
    {
      "produitId": 1,
      "quantite": 10
    }
  ]
}
```

## ⚠️ Notes importantes

1. **Inscription**: Les nouveaux utilisateurs n'ont aucun rôle par défaut
2. **Attribution de rôle**: Seul l'admin peut assigner des rôles
3. **Permissions personnalisées**: L'admin peut modifier les permissions individuellement
4. **Audit**: Toutes les actions sont tracées avec l'email de l'utilisateur
5. **Sécurité**: Les mots de passe sont hashés avec BCrypt

## 🔄 Workflow typique

1. **Inscription** d'un nouvel utilisateur
2. **Attribution d'un rôle** par l'admin
3. **Connexion** et récupération des tokens
4. **Utilisation** de l'API avec les permissions du rôle
5. **Personnalisation** éventuelle des permissions par l'admin