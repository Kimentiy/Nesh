package com.nesh

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongsAdapter(private val songs: List<Song>) : RecyclerView.Adapter<SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)

        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount(): Int {
        return songs.size
    }
}

class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val playPauseButton: ImageButton = view.findViewById(R.id.img_btn_play_pause)
    val titleTextView: TextView = view.findViewById(R.id.text_title)
}

fun SongViewHolder.bind(song: Song) {
    titleTextView.text = song.title
}
