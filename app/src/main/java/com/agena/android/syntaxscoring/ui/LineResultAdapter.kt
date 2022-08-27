package com.agena.android.syntaxscoring.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agena.android.syntaxscoring.databinding.ViewholderResultItemBinding

class LineResultAdapter : RecyclerView.Adapter<LineResultAdapter.ResultItemViewHolder>() {

    private var items = mutableListOf<String>()

    fun addLine(line: String) {
        items.add(line)
        notifyItemInserted(items.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultItemViewHolder {
        val binding = ViewholderResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultItemViewHolder, position: Int) {
        holder.binding.lineResult.text = items[position]
    }

    override fun getItemCount(): Int = items.size

    inner class ResultItemViewHolder(val binding: ViewholderResultItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
