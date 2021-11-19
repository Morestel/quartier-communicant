package com.quartier.quartiercommunicant.repository;

import com.quartier.quartiercommunicant.model.Conference;

import org.springframework.data.repository.CrudRepository;

public interface ConferenceRepository extends CrudRepository<Conference, String> {
    
}
