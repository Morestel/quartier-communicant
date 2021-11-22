package com.quartier.quartiercommunicant.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class DemandeConference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToMany(cascade = CascadeType.REMOVE)
    private List<Conference> listConference;

    public DemandeConference(){
        /* */
    }

    public DemandeConference(List<Conference> listConference) {
        this.listConference = listConference;
    }

    
}
