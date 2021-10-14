package com.quartier.quartiercommunicant.repository;

import java.util.List;

import com.quartier.quartiercommunicant.model.*;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichierRepository extends CrudRepository<Fichier, Integer>{
    
    List<Fichier> findAll();
}