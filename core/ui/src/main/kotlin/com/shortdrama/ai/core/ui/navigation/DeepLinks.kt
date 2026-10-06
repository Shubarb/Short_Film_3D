package com.shortdrama.ai.core.ui.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest

/**
 * Feature modules don't depend on each other, so they navigate through deep links declared
 * in each feature's nav graph. The URIs below must match those graphs.
 */
object DeepLinks {
    const val ARG_FILM_ID = "filmId"

    private const val SCHEME = "shortfilm"

    fun player(filmId: String): Uri = "$SCHEME://player/${Uri.encode(filmId)}".toUri()
}

fun NavController.navigateToPlayer(filmId: String) {
    navigate(NavDeepLinkRequest.Builder.fromUri(DeepLinks.player(filmId)).build())
}
