package com.example;

import com.example.model.Produit;
import com.example.model.Categorie;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

public class App {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-demo");

        // Insertion des produits et catégories
        insererProduits(emf);

        // Lecture de tous les produits
        lireProduits(emf);

        // Mise à jour du prix du Smartphone (id = 2)
        mettreAjourProduit(emf, 2L, new BigDecimal("550.00"));

        // Suppression de la Tablette (id = 3)
        supprimerProduit(emf, 3L);

        // Recherche de produits entre 100 et 1000
        rechercherParPlagePrix(emf, new BigDecimal("100.00"), new BigDecimal("1000.00"));

        emf.close();
    }

    // Insertion des produits et catégories
    private static void insererProduits(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Création des catégories
            Categorie cat1 = new Categorie("Informatique");
            Categorie cat2 = new Categorie("Mobilier");

            em.persist(cat1);
            em.persist(cat2);

            // Création des produits
            Produit p1 = new Produit("Laptop", new BigDecimal("999.99"));
            Produit p2 = new Produit("Smartphone", new BigDecimal("499.99"));
            Produit p3 = new Produit("Tablette", new BigDecimal("299.99"));

            // Lier les produits aux catégories
            p1.setCategorie(cat1);
            p2.setCategorie(cat1);
            p3.setCategorie(cat2);

            // Persister les produits
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);

            em.getTransaction().commit();
            System.out.println("Produits et catégories insérés avec succès !");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Lecture de tous les produits
    private static void lireProduits(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Produit> produits = em.createQuery("SELECT p FROM Produit p", Produit.class)
                    .getResultList();

            System.out.println("\nListe des produits :");
            for (Produit produit : produits) {
                System.out.println(produit + ", Categorie=" +
                        (produit.getCategorie() != null ? produit.getCategorie().getNom() : "Aucune"));
            }

            // Recherche d'un produit par ID
            System.out.println("\nRecherche du produit avec ID=2 :");
            Produit produit = em.find(Produit.class, 2L);
            if (produit != null) {
                System.out.println(produit + ", Categorie=" +
                        (produit.getCategorie() != null ? produit.getCategorie().getNom() : "Aucune"));
            } else {
                System.out.println("Produit non trouvé");
            }
        } finally {
            em.close();
        }
    }

    // Mise à jour du prix d'un produit
    private static void mettreAjourProduit(EntityManagerFactory emf, Long id, BigDecimal nouveauPrix) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Produit produit = em.find(Produit.class, id);
            if (produit != null) {
                produit.setPrix(nouveauPrix);
                System.out.println("Prix mis à jour : " + produit);
            } else {
                System.out.println("Produit non trouvé pour mise à jour");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Suppression d'un produit
    private static void supprimerProduit(EntityManagerFactory emf, Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Produit produit = em.find(Produit.class, id);
            if (produit != null) {
                em.remove(produit);
                System.out.println("Produit supprimé : " + produit);
            } else {
                System.out.println("Produit non trouvé pour suppression");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    // Recherche des produits par plage de prix
    private static void rechercherParPlagePrix(EntityManagerFactory emf, BigDecimal min, BigDecimal max) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Produit> produits = em.createQuery(
                            "SELECT p FROM Produit p WHERE p.prix BETWEEN :min AND :max", Produit.class)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();

            System.out.println("\nProduits entre " + min + " et " + max + " :");
            produits.forEach(p -> System.out.println(p + ", Categorie=" +
                    (p.getCategorie() != null ? p.getCategorie().getNom() : "Aucune")));
        } finally {
            em.close();
        }
    }
}
