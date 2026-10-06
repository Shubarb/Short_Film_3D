package com.shortdrama.ai.core.common.dispatcher

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: AppDispatcher)

enum class AppDispatcher {
    Default,
    IO,
}
