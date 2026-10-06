package com.shortdrama.ai.core.ui.mvi

/** Something the user (or the system) wants to happen: a click, a pull-to-refresh, a screen start. */
interface UiIntent

/** The complete, immutable description of what a screen shows. Rendering is a pure function of it. */
interface UiState

/** A one-off event that must be consumed once: navigation, a toast, a snackbar. */
interface UiEffect
