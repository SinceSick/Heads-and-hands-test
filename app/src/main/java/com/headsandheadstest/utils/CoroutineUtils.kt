package com.headsandheadstest.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.newSingleThreadContext

const val BACK_CONTEXT = "BackContext"

@OptIn(DelicateCoroutinesApi::class)
val coroutineContext = newSingleThreadContext(BACK_CONTEXT)

fun getCoroutineScope() = CoroutineScope(coroutineContext + Job())

fun <T> createPublishFlow() = MutableSharedFlow<T>(0, 1, BufferOverflow.DROP_OLDEST)

fun <T> createBehaviourFlow() = MutableSharedFlow<T>(1, 1, BufferOverflow.DROP_OLDEST)