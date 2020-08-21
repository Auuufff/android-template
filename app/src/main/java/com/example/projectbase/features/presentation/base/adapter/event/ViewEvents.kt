package com.example.projectbase.features.presentation.base.adapter.event

import com.example.projectbase.features.presentation.base.adapter.ListItem
import com.example.projectbase.features.presentation.base.adapter.metadata.EventMetadata

sealed class ViewEvents : ViewEvent {

    data class ClickEvent(
        override val item: ListItem,
        override val metaData: EventMetadata,
        val position: Int
    ) : ViewEvents()
}