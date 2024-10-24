package edu.ucsb.cs156.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/** 
 * This is a JPA entity that represents a UCSBOrganization
 * 
 * A UCSBOganization is an organization at UCSB
 */

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsborganization")
public class UCSBOrganization{
    @Id

    private String orgCode;
    private String orgTranslationShort;
    private String orgTranslation;
    private boolean inactive;
}

