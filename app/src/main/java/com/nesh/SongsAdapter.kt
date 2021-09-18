package com.nesh

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

fun createSongItemGroup(onPlayPauseClicked: (SavedSong) -> Unit): ItemGroup<SavedSong, SongViewHolder> {
    return ItemGroup(layoutResId = R.layout.item_song,
        isMyData = { it is SavedSong },
        createViewHolder = ::SongViewHolder,
        bindViewHolder = { holder, data -> holder.bind(data, onPlayPauseClicked) }
    )
}

class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val playPauseButton: ImageButton = view.findViewById(R.id.img_btn_play_pause)
    val titleTextView: TextView = view.findViewById(R.id.text_title)
}

fun SongViewHolder.bind(song: SavedSong, onPlayPauseClicked: (SavedSong) -> Unit) {
    playPauseButton.setOnClickListener { onPlayPauseClicked(song) }
    titleTextView.text = song.title
}

fun createSearchSongItemGroup(onDownloadClicked: (SearchSong) -> Unit): ItemGroup<SearchSong, SearchSongViewHolder> {
    return ItemGroup(
        layoutResId = R.layout.item_search_song,
        isMyData = { it is SearchSong },
        createViewHolder = ::SearchSongViewHolder,
        bindViewHolder = { holder, data ->
            holder.bind(data, onDownloadClicked)
        }
    )
}

class SearchSongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val downloadButton: ImageButton = view.findViewById(R.id.img_btn_download)
    val titleTextView: TextView = view.findViewById(R.id.text_title)
}

fun SearchSongViewHolder.bind(song: SearchSong, onClick: (SearchSong) -> Unit) {
    titleTextView.text = song.title

    downloadButton.setOnClickListener { onClick(song) }
}
