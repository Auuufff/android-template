package com.example.projectbase.features.presentation.base.adapter.event

import com.example.projectbase.features.presentation.base.adapter.ListItem
import com.example.projectbase.features.presentation.base.adapter.metadata.EventMetadata

interface ViewEvent {
    val item: ListItem
    val metaData: EventMetadata
}