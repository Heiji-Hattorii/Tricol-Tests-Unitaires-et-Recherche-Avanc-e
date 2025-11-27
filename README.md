# Tricol-Tests-Unitaires-et-Recherche-Avanc-e

# 📦 Tricol – Gestion de Stock (FIFO)

## 📝 Description Générale

Ce projet améliore le module de gestion de stock de Tricol, basé sur la méthode **FIFO (First-In, First-Out)**.  
Il vise à garantir la fiabilité du cœur métier à travers une couverture complète en **tests unitaires**, et à fournir une **recherche avancée** des mouvements de stock pour répondre aux besoins d’audit.

---

## ✔️ Partie 1 — Tests Unitaires

Les tests (JUnit + Mockito) doivent couvrir :

### 🔧 1. Mécanisme FIFO – Sortie de stock
- Consommation partielle d’un lot
- Consommation sur plusieurs lots successifs
- Gestion du stock insuffisant
- Sortie épuisant exactement le stock

### 📦 2. Création automatique de lots
- Génération du numéro de lot
- Enregistrement de la date d’entrée
- Stockage du prix d’achat unitaire
- Association avec la réception fournisseur

### 💰 3. Valorisation du stock
- Calcul de la valeur totale du stock
- Gestion multi-lots avec prix différents
- Respect de l’ordre FIFO pour la valorisation

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

## 🛠️ Technologies Utilisées

- **Java 17+**
- **Spring Boot **
- **Spring Data JPA**
- **JUnit **
- **Mockito**
- **Maven**

---

## 🎯 Objectifs du Projet

- Garantir la fiabilité de la logique FIFO
- Assurer une couverture de tests robuste sur la logique métier critique
- Fournir une recherche avancée pour l’audit des mouvements de stock  

