package com.quartier.quartiercommunicant.repository;

import java.util.List;

import com.quartier.quartiercommunicant.model.*;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichierRepository extends CrudRepository<Fichier, Integer>{
    
    List<Fichier> findAll();
    
    Fichier findFichierById(int id);

    @Query("SELECT f FROM Fichier f WHERE f.expediteur = ?1")
    List<Fichier> findFichierByExped(String expediteur);

    @Query("SELECT f FROM Fichier f WHERE f.expediteur = ?1 AND f.destinataire = ?2")
    List<Fichier> findFichierByInfo(String expediteur, String destinataire);
}
