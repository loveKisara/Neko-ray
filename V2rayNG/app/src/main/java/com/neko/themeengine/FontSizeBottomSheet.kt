package com.neko.themeengine

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.neko.v2ray.databinding.ItemFontSizeBinding
import com.neko.v2ray.databinding.SheetFontSizeBinding

class FontSizeBottomSheet : BottomSheetDialogFragment() {

    interface FontSizeChangeListener {
        fun onFontSizeChanged(newSize: FontSize)
    }

    private var _binding: SheetFontSizeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FontSizeAdapter
    private lateinit var engine: ThemeEngine
    private var listener: FontSizeChangeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        engine = ThemeEngine.getInstance(context)
        if (context is FontSizeChangeListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement FontSizeChangeListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetFontSizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FontSizeAdapter(engine.fontSize) { selectedSize ->
            engine.fontSize = selectedSize
            listener?.onFontSizeChanged(selectedSize)
            dismiss()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class FontSizeAdapter(
        private val currentSize: FontSize,
        private val onSizeSelected: (FontSize) -> Unit
    ) : RecyclerView.Adapter<FontSizeAdapter.ViewHolder>() {

        private val sizes = FontSize.values()

        inner class ViewHolder(val binding: ItemFontSizeBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemFontSizeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val size = sizes[position]
            holder.binding.apply {
                textSize.text = size.displayName
                radioButton.isChecked = size == currentSize

                root.setOnClickListener {
                    onSizeSelected(size)
                }
            }
        }

        override fun getItemCount() = sizes.size
    }
}
