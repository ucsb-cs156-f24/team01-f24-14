package edu.ucsb.cs156.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 
 * This is a JPA entity that represents a UCSBDiningCommonsMenuItem
 * 
 * A UCSBDiningCommonsMenuItem is a menu item in the UCSB dining commons menus
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsbdiningcommonsmenuitem")
public class UCSBDiningCommonsMenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    String diningCommonsCode;
    String name;
    String station;
}