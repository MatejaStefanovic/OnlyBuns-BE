package org.onlybuns.repository;

import org.onlybuns.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {

    Optional<Location> findByCoordinateKey(String coordinateKey);
}
