package com.example.murmurbox.recyclerview.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.murmurbox.R
import com.example.murmurbox.entity.SelectedEmotionData
import com.example.murmurbox.recyclerview.data.EmotionData
import com.example.murmurbox.recyclerview.data.WaveData

class WaveAdapter(
    private var items: ArrayList<WaveData>,
    private var defaultWaveColor: Int
) : RecyclerView.Adapter<WaveAdapter.ViewHolder>() {

    private val defaultHeight = 40
    private var isPaused = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wave, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        val params = holder.lnWave.layoutParams
        params.height = item.height
        holder.lnWave.layoutParams = params

        val color = if (isPaused) {
            defaultWaveColor
        } else {
            item.color
        }

        holder.lnWave.background?.mutate()?.setTint(color)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun reset(color: Int) {
        items.forEach {
            it.height = defaultHeight
            it.color = color
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setPaused(paused: Boolean) {
        isPaused = paused
        notifyDataSetChanged()
    }

    fun animateWave(color: Int) {
        items.forEachIndexed { index, item ->
            item.height = (20..120).random()
            item.color = color
            notifyItemChanged(index)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lnWave: LinearLayout = itemView.findViewById(R.id.lnWave)
    }
}