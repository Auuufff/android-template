package com.example.projectbase.features.presentation.base.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.BatchingListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.projectbase.features.presentation.base.adapter.diffutil.Callback
import com.example.projectbase.features.presentation.base.adapter.event.ViewEvents
import com.example.projectbase.features.presentation.base.adapter.metadata.EventMetadata
import com.example.projectbase.features.presentation.base.adapter.viewholder.BaseViewHolder
import com.example.projectbase.features.presentation.base.adapter.viewholder.ViewHolder
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import ru.ldralighieri.corbind.safeOffer
import java.util.*
import kotlin.reflect.KClass

abstract class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    Adapter, CoroutineScope {

    private val job = SupervisorJob()
    private val clickChannel: Channel<ViewEvents.ClickEvent> = Channel(Channel.CONFLATED)
    private val adapterErrorChannel = Channel<Throwable>(Channel.CONFLATED)
    private val updateChannel = BroadcastChannel<List<ListItem>>(Channel.CONFLATED)

    private val listUpdateCallback by lazy {
        BatchingListUpdateCallback(
            AdapterListUpdateCallback(this@BaseAdapter)
        )
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        adapterErrorChannel.safeOffer(e)
    }

    protected val items = mutableListOf<ListItem>()
    protected abstract val layoutResMap: Map<Int, Int>
    protected val KClass<out ListItem>.viewType: Int
        get() = this@viewType.java.name.hashCode()
    protected val anyViewType = ListItem::class.viewType

    final override val coroutineContext =
        Dispatchers.Main.immediate + job + coroutineExceptionHandler

    init {
        observeUpdatingList(updateChannel)
    }

    final override fun getItemViewType(position: Int) = items[position]::class.viewType

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val layoutRes =
            (layoutResMap[viewType] ?: layoutResMap[anyViewType]) ?: onCreateError(viewType)
        val viewHolder = BaseViewHolder(parent, layoutRes) as ViewHolder
        return viewHolder.onCreateViewHolder(viewType)
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).onBindViewHolder(items[position])
    }

    final override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        payloads.forEach { payload ->
            when (payload) {
                is Collection<*> -> payload.forEach { nestedPayload ->
                    nestedPayload?.let {
                        (holder as ViewHolder).onBindViewHolder(
                            items[position],
                            it
                        )
                    }
                }
                else -> (holder as ViewHolder).onBindViewHolder(items[position], payload)
            }
        }
        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads)
    }

    final override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            (holder as ViewHolder).onViewRecycled(items[position])
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            (holder as ViewHolder).onViewDetachedFromWindow(items[position])
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            (holder as ViewHolder).onViewAttachedToWindow(items[position])
        }
    }

    final override fun getItemCount() = items.size

    private fun setData(newItems: List<ListItem>) {
        items.clear()
        items.addAll(newItems)
    }

    fun updateData(itemsList: List<ListItem>) {
        updateChannel.safeOffer(itemsList)
    }

    protected fun onBindError(item: ListItem) {
        layoutResMap[item::class.viewType]
            ?: error("No bind defined for item ${item::class} in adapter ${this::class}")
    }

    protected fun onBindPayloadError(
        item: ListItem,
        payload: Any,
        ignore: List<KClass<*>> = emptyList()
    ) {
        val isIgnoringPayload = ignore.any { it == payload::class }
        if (isIgnoringPayload.not())
            error("No bind defined for item ${item::class} in adapter ${this::class} using payload ${payload::class} with ignoring payload from classes $ignore")
    }

    private fun onCreateError(viewType: Int): Nothing {
        error("No layout res defined for viewType $viewType in adapter ${this::class}")
    }

    protected open fun ViewHolder.onCreateViewHolder(viewType: Int): ViewHolder =
        this@onCreateViewHolder

    protected open fun ViewHolder.onBindViewHolder(item: ListItem): Unit = Unit

    protected open fun ViewHolder.onBindViewHolder(item: ListItem, payload: Any): Unit = Unit

    protected open fun ViewHolder.onViewRecycled(item: ListItem) = Unit

    protected open fun ViewHolder.onViewDetachedFromWindow(item: ListItem) = Unit

    protected open fun ViewHolder.onViewAttachedToWindow(item: ListItem) = Unit

    protected fun ViewHolder.setOnClickListener(
        viewToListen: View? = null,
        metaData: EventMetadata
    ) {
        (viewToListen ?: this.itemView).setOnClickListener {
            doOnPosition { holderPosition ->
                clickChannel.safeOffer(
                    ViewEvents.ClickEvent(
                        items[holderPosition],
                        metaData,
                        holderPosition
                    )
                )
            }
        }
    }

    protected fun ViewHolder.setOnClickListener(
        viewToListen: View?,
        block: (view: View, item: ListItem) -> Unit
    ) {
        viewToListen?.setOnClickListener {
            doOnPosition { holderPosition ->
                block(it, items[holderPosition])
            }
        }
    }

    private fun observeUpdatingList(updateListChannel: BroadcastChannel<List<ListItem>>) = launch {
        updateListChannel
            .openSubscription()
            .consumeAsFlow()
            .mapLatest { newItems ->
                val before = items.toImmutableList()
                val after = newItems.toImmutableList()
                val callback = Callback(before, after)
                DiffUtil.calculateDiff(callback, true) to after
            }
            .flowOn(Dispatchers.IO)
            .onEach { (diffResult, after) ->
                setData(after)
                diffResult.dispatchUpdatesTo(listUpdateCallback)
            }
            .collect()
    }

    private fun <T> List<T>.toImmutableList(): List<T> {
        return Collections.unmodifiableList(toMutableList())
    }
}