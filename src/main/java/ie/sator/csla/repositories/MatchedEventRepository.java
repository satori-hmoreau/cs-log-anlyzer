package ie.sator.csla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ie.sator.csla.models.MatchedEvent;

@Repository
public interface MatchedEventRepository extends JpaRepository<MatchedEvent, Long> {

}
