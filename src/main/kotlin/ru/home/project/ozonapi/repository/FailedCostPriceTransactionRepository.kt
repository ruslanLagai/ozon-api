package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.home.project.ozonapi.entity.FailedCostPriceTransactionEntity

interface FailedCostPriceTransactionRepository : JpaRepository<FailedCostPriceTransactionEntity, Long> {

}