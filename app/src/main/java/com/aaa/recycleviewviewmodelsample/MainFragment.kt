package com.aaa.recycleviewviewmodelsample

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aaa.recycleviewviewmodelsample.databinding.FragmentMainBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private lateinit var _binding: FragmentMainBinding
    private val _viewModel: MainViewModel by viewModels()
    private var dataFlowJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* RecyclerViewのレイアウト指定 */
        _binding.ryvSample.layoutManager = LinearLayoutManager(requireContext())
        /* アダプター定義 */
        val adapter = CustomAdapter()
        _binding.ryvSample.adapter = adapter

        /* StateFlow監視 */
        dataFlowJob = viewLifecycleOwner.lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.RESUMED) {
            _viewModel.dataFlow.collect { newData ->
                adapter.submitList(newData)
            }
        }}

        _binding.btnAdd.setOnClickListener {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') /* 使用する文字の種類 */
            val addstr = (1..4)
                .map { allowedChars.random() }
                .joinToString("")
            _viewModel.addItem(addstr)
        }
        _binding.btnDel.setOnClickListener {
            _viewModel.removeItem(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dataFlowJob.cancel()
    }
}

class CustomAdapter :
        ListAdapter<String, CustomAdapter.CustomViewHolder>(DiffCallback) {
    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.textView.text = getItem(position)
    }

    /* データを更新するメソッド */
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                // アイテムが一意に識別できるなら ID 比較を使う
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                // 中身が同じかどうか
                return oldItem == newItem
            }
        }
    }
}