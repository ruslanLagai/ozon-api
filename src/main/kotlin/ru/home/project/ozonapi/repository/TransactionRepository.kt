package ru.home.project.ozonapi.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import ru.home.project.ozonapi.entity.TransactionEntity
import java.util.Optional

interface TransactionRepository : JpaRepository<TransactionEntity, Long> {

    fun getAllByOperationIdIn(
        operationIds: Collection<String>,
        pageable: Pageable
    ): Slice<TransactionEntity>

    @Modifying
    @Query("delete from TransactionEntity t where t.operationId in :operationIds")
    fun deleteAllByOperationIdIn(operationIds: Collection<String>)

    fun findByOperationId(operationId: String) : Optional<TransactionEntity>
}

