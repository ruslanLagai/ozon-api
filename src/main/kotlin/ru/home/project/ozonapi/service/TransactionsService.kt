package ru.home.project.ozonapi.service

import java.util.function.Supplier

interface TransactionsService {

    fun <T> runInTransaction(supplier: Supplier<T>) : T
}