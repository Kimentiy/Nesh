package com.nesh

import androidx.lifecycle.LiveData


val <T : Any> LiveData<T>.requireValue: T
    get() = value!!
