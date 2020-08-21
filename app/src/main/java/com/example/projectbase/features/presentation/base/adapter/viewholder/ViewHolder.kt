package com.example.projectbase.features.presentation.base.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

/**
 * Parent class for any ViewHolder.
 */
abstract class ViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun doOnPosition(block: (position: Int) -> Unit) {
        val position = adapterPosition
        if (position != RecyclerView.NO_POSITION) block(position)
    }
}