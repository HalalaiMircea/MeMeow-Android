package ro.unibuc.cs.memeow.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ro.unibuc.cs.memeow.databinding.LayoutTemplateItemBinding

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 */
class TemplateRecyclerViewAdapter :
    RecyclerView.Adapter<TemplateRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutTemplateItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val item = values[position]
        holder.binding.templateTitle.text = "Sample memez!"

    }

    override fun getItemCount(): Int = 32

    inner class ViewHolder(internal val binding: LayoutTemplateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val action = TemplateListFragmentDirections.actionSelectTemplate()
                it.findNavController().navigate(action)
            }
        }

        override fun toString(): String {
            return super.toString() + " '" + binding.templateTitle + "'"
        }
    }
}