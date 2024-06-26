package ru.home.project.ozonapi.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

/**
 * @author rlagay
 */
@Entity
data class TelegramChatEntity(

    @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long? = 1,

    @Column(name = "chat_id", nullable = false) val chatId: Long,

    @Column(name = "position_name", nullable = true, unique = false) var positionName: String,

    @Column(name = "deliveryId", nullable = true, unique = false) var deliveryId: Long = 0,

    @Column(name = "from_column", length = 50) var from: OffsetDateTime? = null,

    @Column(name = "to_column", length = 50) var to: OffsetDateTime? = null,

    @Column(name = "state") var state: Boolean = true,

    @Enumerated(value = EnumType.STRING) @Column(name = "action_type") var action: ActionType? = null
)
