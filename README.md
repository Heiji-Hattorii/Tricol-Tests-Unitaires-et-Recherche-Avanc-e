# Tricol:Security - Tests unitaires et recherche avancé 

# 📦 Tricol – Gestion de Stock (FIFO)

## 📝 Description Générale

Ce projet améliore le module de gestion de stock de Tricol, basé sur la méthode **FIFO (First-In, First-Out)**.  
Il vise à garantir la fiabilité du cœur métier à travers une couverture complète en **tests unitaires**, et à fournir une **recherche avancée** des mouvements de stock pour répondre aux besoins d'audit.

---

## ✔️ Partie 1 — Tests Unitaires

Les tests (JUnit + Mockito) doivent couvrir :

### 🔧 1. Mécanisme FIFO – Sortie de stock
- Consommation partielle d'un lot
- Consommation sur plusieurs lots successifs
- Gestion du stock insuffisant
- Sortie épuisant exactement le stock

### 📦 2. Création automatique de lots
- Génération du numéro de lot
- Enregistrement de la date d'entrée
- Stockage du prix d'achat unitaire
- Association avec la réception fournisseur

### 💰 3. Valorisation du stock
- Calcul de la valeur totale du stock
- Gestion multi-lots avec prix différents
- Respect de l'ordre FIFO pour la valorisation

### 🔄 4. Transition de statut (Bon de sortie)
Lors du passage en VALIDÉ :
- Création des mouvements de stock
- Mise à jour des quantités restantes dans les lots
- Enregistrement des informations de validation (date, utilisateur)

---

## 🔍 Partie 2 — Recherche Avancée des Mouvements

La recherche avancée est implémentée avec **Spring Data JPA Specifications**, permettant un filtrage multi-critères sur :

- La période (date début / date fin)
- Le produit (ID ou référence)
- Le type de mouvement (ENTREE / SORTIE)
- Le numéro de lot
- La pagination (`page` et `size`)

---

## 🔐 Partie 3 — Tests et Sécurité

### 🛡️ Système d'Authentification et d'Autorisation

Suite au développement du module de gestion des stocks, l'application nécessite un système de sécurité robuste pour protéger les données sensibles (informations fournisseurs, prix d'achat, valorisation des stocks).

#### Fonctionnalités de Sécurité Implémentées :

**🔑 Authentification JWT**
- Login/Register avec tokens JWT
- Refresh token pour renouvellement automatique
- Gestion des sessions sécurisées

**👥 Gestion des Rôles et Permissions**
- **4 rôles distincts** : ADMIN, RESPONSABLE_ACHATS, MAGASINIER, CHEF_ATELIER
- **Permissions dynamiques** : L'administrateur peut personnaliser les permissions individuelles
- **Principe de fonctionnement** :
  1. Inscription sans rôle par défaut
  2. Attribution de rôle par l'administrateur
  3. Personnalisation des permissions selon les besoins

**🔒 Protection des Endpoints**
- Sécurisation selon la matrice des permissions
- Contrôle d'accès basé sur les rôles (RBAC)
- Validation des autorisations en temps réel

**📊 Système d'Audit**
- Traçabilité des actions sensibles (qui a fait quoi et quand)
- Journalisation des connexions/déconnexions
- Enregistrement des modifications de permissions
- Logs d'audit consultables par les administrateurs

#### Exemple Concret de Gestion des Permissions :
```
Utilisateur : Amine (Rôle : MAGASINIER)
- Permissions par défaut : Créer bons de sortie, consulter stock, réceptionner commandes
- Action admin : Retrait de la permission "Créer bons de sortie"
- Résultat : Amine garde son rôle mais perd cette permission spécifique
```

**🧪 Tests Unitaires de Sécurité**
- Tests d'authentification (register, login, refresh token)
- Tests d'autorisation par rôle
- Tests de gestion des permissions
- Couverture complète avec JUnit + Mockito

---

## 🛠️ Technologies Utilisées

- **Java 17+**
- **Spring Boot**
- **Spring Security** (JWT)
- **Spring Data JPA**
- **JUnit**
- **Mockito**
- **Maven**

---

## 🎯 Objectifs du Projet

- Garantir la fiabilité de la logique FIFO
- Assurer une couverture de tests robuste sur la logique métier critique
- Fournir une recherche avancée pour l'audit des mouvements de stock
- **Sécuriser l'accès aux données sensibles**
- **Implémenter un système d'authentification et d'autorisation robuste**
- **Tracer toutes les actions critiques pour l'audit**