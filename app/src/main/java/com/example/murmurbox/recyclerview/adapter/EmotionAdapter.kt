package com.example.murmurbox.recyclerview.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.murmurbox.R
import com.example.murmurbox.entity.SelectedEmotionData
import com.example.murmurbox.recyclerview.data.EmotionData

class EmotionAdapter(
    private var items: ArrayList<EmotionData>,
    private val onItemClick: (SelectedEmotionData) -> Unit
) : RecyclerView.Adapter<EmotionAdapter.ViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emotion, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val isSelected = position == selectedPosition

        holder.tvTitle.text = item.emotion
        holder.tvDescription.text = item.description
        holder.imgEmotion.setImageResource(getEmotionIcon(item.iconName))
        holder.crdEmotion.setCardBackgroundColor(if (isSelected) item.borderColor.toColorInt() else Color.WHITE)
        holder.crdEmotionBackground.setCardBackgroundColor(if (isSelected) item.backgroundColor.toColorInt() else Color.WHITE)
        val color = if (isSelected) {
            item.borderColor.toColorInt()
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.text_color_gray)
        }
        holder.tvTitle.setTextColor(color)


        holder.crdEmotion.setOnClickListener {
            val previousPosition = selectedPosition
            val selectedEmotionData = SelectedEmotionData(
                id = item.id,
                emotion = item.emotion,
                backgroundColor = item.backgroundColor,
                borderColor = item.borderColor
            )
            selectedPosition = holder.bindingAdapterPosition
            onItemClick(selectedEmotionData)

            if (previousPosition != -1) notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val crdEmotion: CardView = itemView.findViewById(R.id.crdEmotion)
        val crdEmotionBackground: CardView = itemView.findViewById(R.id.crdEmotionBackground)
        val imgEmotion: ImageView = itemView.findViewById(R.id.imgEmotion)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    private fun getEmotionIcon(name: String): Int {
        return when (name.lowercase()) {
            "ic_happy" -> R.drawable.ic_happy
            "ic_sad" -> R.drawable.ic_sad
            "ic_angry" -> R.drawable.ic_angry
            "ic_calm" -> R.drawable.ic_calm
            "ic_lonely" -> R.drawable.ic_lonely
            "ic_anxious" -> R.drawable.ic_anxious
            else -> R.drawable.murmur_icon
        }
    }
}