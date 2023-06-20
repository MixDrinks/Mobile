package org.mixdrinks.ui.tag

import org.mixdrinks.data.SnapshotRepository

internal class CommonTagNameProvider(
    private val snapshotRepository: SnapshotRepository,
) {

    suspend fun getName(commonTag: CommonTag): String? {
        return when (commonTag.type) {
            CommonTag.Type.TAG -> {
                snapshotRepository.get()
                    .tags.find { it.id.id == commonTag.id }?.name
            }

            CommonTag.Type.TASTE -> {
                snapshotRepository.get()
                    .tastes.find { it.id.id == commonTag.id }?.name
            }
        }
    }
}
