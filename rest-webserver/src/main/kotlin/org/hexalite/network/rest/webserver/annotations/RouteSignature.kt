package org.hexalite.network.rest.webserver.annotations

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlin.reflect.KFunction
import kotlin.reflect.KSuspendFunction1

annotation class Get(val path: String)

annotation class Post(val path: String)

annotation class Patch(val path: String)

annotation class Put(val path: String)

annotation class Delete(val path: String)

typealias RouteFuncSignature = KSuspendFunction1<PipelineContext<Unit, ApplicationCall>, Unit>

internal infix fun <T> Route.use(fnc: T) where T: RouteFuncSignature, T: KFunction<Unit> {
    for (annotation in fnc.annotations) {
        when (annotation) {
            is Get -> get(annotation.path) { fnc(this) }
            is Post -> post(annotation.path) { fnc(this) }
            is Put -> put(annotation.path) { fnc(this) }
            is Patch -> patch(annotation.path) { fnc(this) }
            is Delete -> delete(annotation.path) { fnc(this) }
        }
    }
}