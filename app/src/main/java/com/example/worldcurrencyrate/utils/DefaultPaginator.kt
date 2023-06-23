package com.example.worldcurrencyrate.utils

class DefaultPaginator<Key, Item1, Item2>(
    private val initialKey: Key,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (nextKey: Key) -> Map<Item1, Item2>,
    private inline val getNextKey: suspend (Map<Item1, Item2>) -> Key,
    private inline val onSuccess: suspend (items: Map<Item1, Item2>, newKey: Key) -> Unit
): Paginator<Key, Item1, Item2> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    override suspend fun loadNextItem() {

        if (isMakingRequest) {
            return
        }
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        onLoadUpdated(false)

        currentKey = getNextKey(result)

        onSuccess(result, currentKey)
        onLoadUpdated(false)

    }

    override fun reset() {
        currentKey = initialKey
    }

}





