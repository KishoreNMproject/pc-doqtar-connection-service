package com.pcdoqtar.application.repository;

import com.pcdoqtar.application.entities.Diagnosticrecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DiagnosticrecordRepository extends JpaRepository<Diagnosticrecord, Long> {

    Optional<Diagnosticrecord> findFirstByFirebaseUidOrderByCapturedAtDesc(String firebaseUid);

    List<Diagnosticrecord> findByFirebaseUidOrderByCapturedAtDesc(String firebaseUid, Pageable pageable);

    List<Diagnosticrecord> findByFirebaseUidAndSessionIdOrderByCapturedAtDesc(String firebaseUid, String sessionId);

    long countByFirebaseUid(String firebaseUid);
}