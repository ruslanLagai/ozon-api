package ru.home.project.ozonapi.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.home.project.ozonapi.entity.FbsOrderEntity
import java.util.*

/**
 * @author rlagay
 */
interface FbsOrderRepository: JpaRepository<FbsOrderEntity, UUID> {

    fun getByNumberAndSubtracted(number: String, subtracted: Boolean) : FbsOrderEntity?

    fun findFirstByOrderByDateDesc() : FbsOrderEntity?

}