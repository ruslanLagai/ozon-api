package ru.home.project.ozonapi.service.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.home.project.ozonapi.service.TransactionsService
import java.util.function.Supplier

/**
 * @author rlagay
 */
@Service
class TransactionsServiceImpl : TransactionsService {

    @Transactional(propagation = Propagation.REQUIRED)
    override fun <T> runInTransaction(supplier: Supplier<T>) : T {
        return supplier.get();
    }

}