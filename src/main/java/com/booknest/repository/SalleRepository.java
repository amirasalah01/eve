package com.booknest.repository;

import com.booknest.model.Salle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {

    List<Salle> findByDisponibleTrue();

    List<Salle> findByCapaciteGreaterThanEqual(int capacite);

    List<Salle> findByNomContainingIgnoreCase(String nom);

    List<Salle> findByLocalisationContainingIgnoreCase(String localisation);

    Page<Salle> findAll(Pageable pageable);

    @Query("SELECT s FROM Salle s WHERE " +
           "(:nom IS NULL OR LOWER(s.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:localisation IS NULL OR LOWER(s.localisation) LIKE LOWER(CONCAT('%', :localisation, '%'))) AND " +
           "(:minCapacite IS NULL OR s.capacite >= :minCapacite)")
    Page<Salle> search(@Param("nom") String nom,
                       @Param("localisation") String localisation,
                       @Param("minCapacite") Integer minCapacite,
                       Pageable pageable);
}
