package com.example.talkback.poc.models

data class ComponentRenderer(val cards: List<Card>, val id: Int, val viewType: Int) : ItemModel {
    override val diffId: String = id.toString()
}