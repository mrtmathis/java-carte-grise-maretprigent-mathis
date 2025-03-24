package controllers;

import models.Posseder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;

public class PossederController {
    public List<Posseder> getAllPosseder() {
        List<Posseder> possederList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM POSSEDER";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Posseder posseder = new Posseder(
                    rs.getInt("id_proprietaire"),
                    rs.getInt("id_vehicule"),
                    rs.getDate("date_debut_propriete"),
                    rs.getDate("date_fin_propriete")
                );
                possederList.add(posseder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return possederList;
    }

    public void addPosseder(Posseder posseder) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO POSSEDER (id_proprietaire, id_vehicule, date_debut_propriete, date_fin_propriete) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, posseder.getIdProprietaire());
            pstmt.setInt(2, posseder.getIdVehicule());
            pstmt.setDate(3, new java.sql.Date(posseder.getDateDebutPropriete().getTime()));
            pstmt.setDate(4, posseder.getDateFinPropriete() != null ? new java.sql.Date(posseder.getDateFinPropriete().getTime()) : null);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePosseder(Posseder posseder) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Vérifier si l'entrée existe avant de mettre à jour
            String checkQuery = "SELECT COUNT(*) FROM POSSEDER WHERE id_proprietaire = ? AND id_vehicule = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, posseder.getIdProprietaire());
            checkStmt.setInt(2, posseder.getIdVehicule());
            ResultSet rs = checkStmt.executeQuery();
    
            if (rs.next() && rs.getInt(1) > 0) {  // Si l'entrée existe
                String query = "UPDATE POSSEDER SET date_debut_propriete = ?, date_fin_propriete = ? WHERE id_proprietaire = ? AND id_vehicule = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setDate(1, new java.sql.Date(posseder.getDateDebutPropriete().getTime()));
                pstmt.setDate(2, posseder.getDateFinPropriete() != null ? new java.sql.Date(posseder.getDateFinPropriete().getTime()) : null);
                pstmt.setInt(3, posseder.getIdProprietaire());
                pstmt.setInt(4, posseder.getIdVehicule());
                
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated == 0) {
                    System.out.println("⚠ Aucune mise à jour effectuée !");
                } else {
                    System.out.println("✅ Mise à jour réussie pour id_proprietaire: " + posseder.getIdProprietaire() + " et id_vehicule: " + posseder.getIdVehicule());
                }
            } else {
                System.out.println("❌ Erreur : L'entrée à mettre à jour n'existe pas !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deletePosseder(int idProprietaire, int idVehicule) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM POSSEDER WHERE id_proprietaire = ? AND id_vehicule = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idProprietaire);
            pstmt.setInt(2, idVehicule);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getIdProprietaire(String nomProprietaire) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id_proprietaire FROM PROPRIETAIRE WHERE nom = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nomProprietaire);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_proprietaire");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getIdVehiculeParModele(String nomModele) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT v.id_vehicule FROM VEHICULE v JOIN MODELE m ON v.id_modele = m.id_modele WHERE m.nom_modele = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nomModele);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_vehicule");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getNomProprietaire(int idProprietaire) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT nom FROM PROPRIETAIRE WHERE id_proprietaire = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idProprietaire);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nom");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Inconnu";
    }

    public String getNomModele(int idVehicule) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT m.nom_modele FROM MODELE m JOIN VEHICULE v ON m.id_modele = v.id_modele WHERE v.id_vehicule = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idVehicule);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nom_modele");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Inconnu";
    }
}
