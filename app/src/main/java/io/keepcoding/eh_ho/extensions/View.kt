package io.keepcoding.eh_ho.extensions

import android.view.LayoutInflater
import android.view.View

inline val View.inflater: LayoutInflater
    get() = LayoutInflater.from(context)