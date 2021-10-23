package com.quartier.quartiercommunicant.repository;

import java.util.List;

import com.quartier.quartiercommunicant.model.Message;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MessageRepository extends CrudRepository<Message, String>{
    
    List<Message> findAll();
}
