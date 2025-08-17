package com.aaa.recycleviewviewmodelsample

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _dataFlow = MutableStateFlow<List<String>>(mutableListOf())
    val dataFlow = _dataFlow.asStateFlow()

    init {
        // 初期データを設定
        val initialList = mutableListOf<String>()
        for (idx in 1..3) {
            initialList.add("アイテム$idx")
        }
        _dataFlow.value = initialList
    }

    /**
     * リストに新しいアイテムを追加するメソッド
     */
    fun addItem(item: String) {
        _dataFlow.update { currentList ->
            currentList + item /* 新しいリストを生成して要素を追加 */
        }
    }

    /**
     * リストからアイテムを削除するメソッド
     */
    fun removeItem(position: Int) {
        _dataFlow.update { currentList ->
            if (position < currentList.size) {
                currentList.toMutableList().apply { removeAt(position) }
            } else {
                currentList /* 変更がない場合は元のリストを返す */
            }
        }
    }
}