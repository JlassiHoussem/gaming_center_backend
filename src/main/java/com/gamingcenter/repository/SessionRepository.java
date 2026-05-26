package com.gamingcenter.repository;

import com.gamingcenter.entity.Session;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    @EntityGraph(attributePaths = {"device"})
    List<Session> findByDeviceId(Long deviceId);

    @EntityGraph(attributePaths = {"device"})
    Optional<Session> findByDeviceIdAndActiveTrue(Long deviceId);

    @EntityGraph(attributePaths = {"device"})
    List<Session> findByShiftId(Long shiftId);

    @EntityGraph(attributePaths = {"device"})
    List<Session> findByActiveTrue();

    @EntityGraph(attributePaths = {"device"})
    @Override
    List<Session> findAll();
}
