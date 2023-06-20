package org.mixdrinks.data

import org.mixdrinks.dto.TagDto
import org.mixdrinks.dto.TagId

internal class TagsRepository(
    private val snapshotRepository: SnapshotRepository,
) {

    suspend fun getTags(tagId: List<TagId>): List<TagDto> {
        return snapshotRepository.get().tags.filter { tagDto -> tagId.contains(tagDto.id) }
    }
}
