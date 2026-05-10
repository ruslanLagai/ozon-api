package ru.home.project.ozonapi.entity

import jakarta.persistence.*

/**
 * @author rlagay
 */
@Entity(name = "telegram_user_entity")
class TelegramUserEntity(

    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = 1,

    @Column(name = "username", nullable = false) var username: String,

    @Column(name = "name", nullable = true, unique = false) var name: String
)
