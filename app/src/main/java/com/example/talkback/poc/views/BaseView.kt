/*
 * Copyright © 2020, Discovery Networks International
 * Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package com.example.talkback.poc.views

interface BaseView<T> {
    fun bindData(data: T)
}
