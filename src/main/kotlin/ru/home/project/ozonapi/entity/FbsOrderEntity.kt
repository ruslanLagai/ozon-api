package ru.home.project.ozonapi.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.util.UUID

/**
 * @author rlagay
 */
@Entity
data class FbsOrderEntity(

    @Id @GeneratedValue(strategy = GenerationType.UUID) val id: UUID? = null,

    @Column(name = "number", nullable = false, unique = true) val number: String,

    @Column(name = "order_date", nullable = false, unique = true) val date: LocalDate,

    @Column(name = "subtracted", nullable = false, unique = true) val subtracted: Boolean = true
)
