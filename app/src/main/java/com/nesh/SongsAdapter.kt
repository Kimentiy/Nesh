package com.nesh

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class SavedSongItem(
    val song: SavedSong,
    val isPlaying: Boolean
)

fun createSongItemGroup(onPlayPauseClicked: (SavedSongItem) -> Unit): ItemGroup<SavedSongItem, SongViewHolder> {
    return ItemGroup(layoutResId = R.layout.item_song,
        isMyData = { it is SavedSongItem },
        createViewHolder = ::SongViewHolder,
        bindViewHolder = { holder, data -> holder.bind(data, onPlayPauseClicked) }
    )
}

class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val playPauseButton: ImageButton = view.findViewById(R.id.img_btn_play_pause)
    val titleTextView: TextView = view.findViewById(R.id.text_title)
}

fun SongViewHolder.bind(item: SavedSongItem, onPlayPauseClicked: (SavedSongItem) -> Unit) {
    playPauseButton.setImageResource(if (item.isPlaying) R.drawable.ic_pause_white else R.drawable.ic_play_white)
    playPauseButton.setOnClickListener { onPlayPauseClicked(item) }
    titleTextView.text = item.song.title
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
