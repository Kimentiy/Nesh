package com.nesh

sealed class Result<T> {

    class Successful<T>(val value: T) : Result<T>()
    class Error<T>(val e: Exception) : Result<T>()
}
