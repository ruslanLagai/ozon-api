package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.home.project.ozonapi.entity.CrossDocTransactionEntity
import java.util.Optional

/**
 * @author rlagay
 */
interface CrossDocTransactionEntityRepository : JpaRepository<CrossDocTransactionEntity, Long> {

    fun findByOrderId(operationId: String): Optional<CrossDocTransactionEntity>
}

