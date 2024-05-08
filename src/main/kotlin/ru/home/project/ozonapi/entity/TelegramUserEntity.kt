package ru.home.project.ozonapi.entity

import jakarta.persistence.*

/**
 * @author rlagay
 */
@Entity
data class TelegramUserEntity(

    @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long? = 1,

    @Column(name = "username", nullable = false) val username: String,

    @Column(name = "name", nullable = true, unique = false) val name: String
)
