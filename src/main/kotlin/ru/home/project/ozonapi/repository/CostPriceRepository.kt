package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.home.project.ozonapi.entity.CostPriceEntity
import java.util.UUID

/**
 * @author rlagay
 */
interface CostPriceRepository : JpaRepository<CostPriceEntity, UUID> {

}

