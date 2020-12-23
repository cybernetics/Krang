/*
 * Copyright (C) 2020 Ivan Milisavljevic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.herman.krang.runtime

object Krang {

    private val tracers = mutableListOf<Tracer>()

    fun addTracer(tracer: Tracer) {
        tracers.add(tracer)
    }

    fun removeTracer(tracer: Tracer) {
        tracers.remove(tracer)
    }

    fun trace(function: String, vararg params: Any?) {
        tracers.forEach { it.trace(function, params) }
    }
}