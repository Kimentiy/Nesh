package com.nesh

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

fun createSongItemGroup(): ItemGroup<Song, SongViewHolder> {
    return ItemGroup(layoutResId = R.layout.item_song,
        isMyData = { it is Song },
        createViewHolder = ::SongViewHolder,
        bindViewHolder = { holder, data -> holder.bind(data) }
    )
}

class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val playPauseButton: ImageButton = view.findViewById(R.id.img_btn_play_pause)
    val titleTextView: TextView = view.findViewById(R.id.text_title)
}

fun SongViewHolder.bind(song: Song) {
    titleTextView.text = song.title
}
