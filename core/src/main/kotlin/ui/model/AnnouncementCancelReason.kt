package ui.model

data class AnnouncementCancelReasonOption(
    val id: Int,
    val title: String,
    val reason: AnnouncementCancelReason
)

enum class AnnouncementCancelReason(
    val id: Int,
    val title: String
) {
    OwnerFound(
        id = 0,
        title = "Владелец нашелся"
    ),
    KeptPet(
        id = 1,
        title = "Оставил себе"
    ),
    GavePetAway(
        id = 2,
        title = "Отдал другому"
    ),
    NoResponses(
        id = 3,
        title = "Никто не откликается"
    ),
    PetFound(
        id = 0,
        title = "Питомец нашелся"
    ),
    Other(
        id = 1,
        title = "Другое"
    );

    fun toOption(
        id: Int = this.id,
        title: String = this.title
    ): AnnouncementCancelReasonOption {
        return AnnouncementCancelReasonOption(
            id = id,
            title = title,
            reason = this
        )
    }

    companion object {
        val missingAnnouncementOptions: List<AnnouncementCancelReasonOption> = listOf(
            PetFound.toOption(id = 0),
            Other.toOption(id = 1)
        )

        val foundAnnouncementOptions: List<AnnouncementCancelReasonOption> = listOf(
            OwnerFound.toOption(id = 0),
            KeptPet.toOption(id = 1),
            GavePetAway.toOption(id = 2),
            NoResponses.toOption(id = 3),
            Other.toOption(id = 4)
        )

        val defaultOptions: List<AnnouncementCancelReasonOption> = foundAnnouncementOptions

    }
}
