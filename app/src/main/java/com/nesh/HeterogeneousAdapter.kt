package com.nesh

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class HeterogeneousAdapter(
    itemGroups: List<ItemGroup<*, *>>,
    initialData: List<Any> = emptyList()
) : Adapter<ViewHolder>() {

    @Suppress("UNCHECKED_CAST")
    private val itemGroups = itemGroups as List<ItemGroup<Any, ViewHolder>>

    var data: List<Any> = initialData
        set(value) {
            field = value

            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val group = itemGroups[viewType]

        val view = LayoutInflater.from(parent.context).inflate(group.layoutResId, parent, false)

        return group.createViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        val itemGroup = itemGroups.first { it.isMyData(item) }

        itemGroup.bindViewHolder(holder, item)
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return groupIndexFor(data[position])
    }

    private fun groupIndexFor(item: Any): Int {
        return itemGroups.indexOfFirst { it.isMyData(item) }
            .takeIf { it >= 0 }
            ?: throw Exception("No suitable item group for $item")
    }

}

class ItemGroup<D : Any, VH : ViewHolder>(
    @LayoutRes val layoutResId: Int,
    val isMyData: (D) -> Boolean,
    val createViewHolder: (View) -> VH,
    val bindViewHolder: (VH, D) -> Unit
)
