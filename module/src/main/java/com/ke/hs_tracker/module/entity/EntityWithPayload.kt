package com.ke.hs_tracker.module.entity

data class EntityWithPayload(
    val entity: Entity,
    private val payloads: MutableMap<String, String> = mutableMapOf()
) {
    fun add(pair: Pair<String, String>) {
        payloads[pair.first] = pair.second
    }

    fun getPayloads(): Map<String, String> = payloads
}
