package ru.home.project.ozonapi.service.impl

import org.jvnet.hk2.annotations.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.home.project.ozonapi.repository.PositionRepository
import ru.home.project.ozonapi.service.CostPriceService

/**
 * @author rlagay
 */
@Service
class CostPriceServiceImpl (
    val positionRepository: PositionRepository
) : CostPriceService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(CostPriceServiceImpl::class.java)
    }

    override fun getFifoCostPrice(ozonId: String, artikul: String): Double {
        positionRepository.getPositionEntityByOzonId(ozonId).let {

        }
        return 0.0
    }

    override fun setCostPrice(ozonId: String, artikul: String): Double {


        return 0.0
    }

}