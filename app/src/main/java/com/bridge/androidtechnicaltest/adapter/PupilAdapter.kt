package com.bridge.androidtechnicaltest.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bridge.androidtechnicaltest.R
import com.bridge.androidtechnicaltest.databinding.ItemPupilBinding
import com.bridge.androidtechnicaltest.db.Pupil

class PupilAdapter() : RecyclerView.Adapter<PupilAdapter.ViewHolder>() {

    lateinit var onItemClick: ((Pupil) -> Unit?)

    class ViewHolder(val binding: ItemPupilBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPupilBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val pupil = differ.currentList[position]

        holder.binding.pupilPhotoImageView.load(pupil.image) {
            placeholder(R.drawable.ic_profile_placeholder)
            error(R.drawable.ic_profile_placeholder)
            crossfade(true)
        }

        holder.binding.pupilNameTextView.text = pupil.name
        holder.binding.pupilCountryTextView.text = pupil.country

        holder.itemView.setOnClickListener {
            onItemClick.invoke(pupil)
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Pupil>() {
        override fun areItemsTheSame(oldItem: Pupil, newItem: Pupil): Boolean {
            return oldItem.pupilId == newItem.pupilId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Pupil, newItem: Pupil): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this@PupilAdapter, diffUtil)
}