package com.example.projectbase.features.presentation.base.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.example.projectbase.features.presentation.base.adapter.ListItem

/**
 * A parent class used by DiffUtil while calculating the diff between two lists.
 */
class Callback(
    private val before: List<ListItem>,
    private val after: List<ListItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = before.size

    override fun getNewListSize(): Int = after.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        before[oldItemPosition].areItemsTheSame(after[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        before[oldItemPosition].areContentsTheSame(after[newItemPosition])

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? =
        before[oldItemPosition].getChangePayload(after[newItemPosition])
}