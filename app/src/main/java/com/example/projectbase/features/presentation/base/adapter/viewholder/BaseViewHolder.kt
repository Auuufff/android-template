package com.example.projectbase.features.presentation.base.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * Default and single RecyclerView ViewHolder implementation.
 */
class BaseViewHolder(parent: ViewGroup, @LayoutRes layoutId: Int) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
)