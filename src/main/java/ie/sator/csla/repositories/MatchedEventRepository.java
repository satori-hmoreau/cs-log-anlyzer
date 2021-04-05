package ie.sator.csla.repositories;
/*
 * Copyright (C) Satori Ltd. 2021.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ie.sator.csla.models.MatchedEvent;

/**
 * JPA repository for MatchedEvents.
 *
 */
@Repository
public interface MatchedEventRepository extends JpaRepository<MatchedEvent, Long> {

}
