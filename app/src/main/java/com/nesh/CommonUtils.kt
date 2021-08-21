package com.nesh

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun showAlertDialog(
    context: Context,
    message: String
): DialogResult = suspendCoroutine { continuation ->
    AlertDialog.Builder(context)
        .setMessage(message)
        .setPositiveButton("Ok") { _, _ ->
            continuation.resume(DialogResult.Accepted)
        }
        .setOnCancelListener {
            continuation.resume(DialogResult.Dismissed)
        }
        .show()
}

fun Context.blockUiAndDo(
    scope: CoroutineScope,
    action: suspend () -> Unit
) {
    val view = LayoutInflater.from(this).inflate(R.layout.diolog_blocking, null)

    val dialog = AlertDialog
        .Builder(this)
        .setView(view)
        .setCancelable(false)
        .create()

    dialog.show()

    scope.launch {
        action()
    }.invokeOnCompletion {
        dialog.cancel()
    }
}

sealed class DialogResult {
    object Accepted : DialogResult()
    object Dismissed : DialogResult()
}

fun createDividerDecoration(
    context: Context,
    layoutManager: LinearLayoutManager
): DividerItemDecoration {
    return DividerItemDecoration(context, layoutManager.orientation).apply {
        ContextCompat.getDrawable(
            context,
            R.drawable.divider
        )!!
    }
}
