package com.example.projectbase.features.presentation.base.adapter

/**
 * A parent class for any recycler view item.
 */
interface ListItem {

    fun areItemsTheSame(other: ListItem): Boolean =
        this::class == other::class && this.getUniqueProperty() == other.getUniqueProperty()

    fun areContentsTheSame(other: ListItem): Boolean = this == other

    fun getChangePayload(other: ListItem): Any = Unit

    fun getUniqueProperty(): Any = this::class.toString()
}