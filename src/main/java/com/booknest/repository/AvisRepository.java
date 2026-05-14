package com.booknest.repository;

import com.booknest.model.Avis;
import com.booknest.model.Salle;
import com.booknest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Long> {

    List<Avis> findBySalle(Salle salle);

    List<Avis> findByUser(User user);

    @Query("SELECT AVG(a.note) FROM Avis a WHERE a.salle = :salle")
    Double avgNoteBySalle(@Param("salle") Salle salle);
}
