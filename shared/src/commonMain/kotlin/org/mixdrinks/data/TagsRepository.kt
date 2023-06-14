package org.mixdrinks.data

import org.mixdrinks.dto.SnapshotDto
import org.mixdrinks.dto.TagDto
import org.mixdrinks.dto.TagId

class TagsRepository(
    private val snapshot: suspend () -> SnapshotDto,
) {

  suspend fun getTags(tagId: List<TagId>): List<TagDto> {
    return snapshot().tags.filter { tagDto -> tagId.contains(tagDto.id) }
  }
}
