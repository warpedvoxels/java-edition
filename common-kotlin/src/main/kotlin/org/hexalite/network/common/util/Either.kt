package org.hexalite.network.common.util

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A wrapper for a value that may be two distinct types. It is inspired by the functional
 * programming paradigm, and it is used to avoid the usage of exceptions on Hexalite and
 * also to represent ProtoBuf's oneof types that have a maximum of two values.
 *
 * You can build an [Either] type by using one of the functions on companion:
 * * [Either.left]   to create an [Either] type bound at left.
 * * [Either.right]  to create an [Either] type bound at right.
 * * [Either.decide] to create an [Either] type based on the given value and generics.
 *
 * An [Either] type is pure, declarative and immutable. You can create another [Either]
 * types from a single one by using map functions:
 * * [Either.mapLeft]  to create another [Either] type by using the bound type at [left]
 *                     as a transform function.
 * * [Either.mapRight] to create another [Either] type by using the bound type at [right]
 *                     as a transform function.
 *
 * @since 0.1.0
 * @author Pedro Henrique
 * @author Gabriel
 * @param L The required type to the value be bound at left.
 * @param R The required type to the value be bound at right.
 */
@Serializable(with = Either.Serializer::class)
data class Either<L, R> private constructor(private var left: L? = null, private var right: R? = null) {
    init {
        require((left != null && right == null) || (left == null && right != null))
    }

    /**
     * Returns whether this [Either] type is bound to left.
     */
    fun isLeft() = left != null

    /**
     * Returns whether this [Either] type is bound to right.
     */
    fun isRight() = right != null

    /**
     * Returns the value bound at the left type ([L]) or null if it is not bound
     * at the given position.
     */
    fun leftOrNull() = left

    /**
     * Returns the value bound at the right type ([R]) or null if it is not bound
     * at the given position.
     */
    fun rightOrNull() = left

    /**
     * Returns the value bound at the left type ([L]) or throws a
     * [BoundTypeMismatchException] if the value is not bound at the given position.
     */
    fun left() = left ?: throw BoundTypeMismatchException('L')

    /**
     * Returns the value bound at the right type ([R]) or throws a
     * [BoundTypeMismatchException] if the value is not bound at the given position.
     */
    fun right() = right ?: throw BoundTypeMismatchException('R')

    /**
     * Returns the value bound at the left type ([L]) or throws the exception returned
     * by the given [error] function. This function is highly inspired by Rust's
     * `Result#expect`.
     */
    fun leftOrThrow(error: () -> Throwable) = leftOrNull() ?: throw error()

    /**
     * Returns the value bound at the right type ([R]) or throws the exception returned
     * by the given [error] function. This function is highly inspired by Rust's
     * `Result#expect`.
     */
    fun rightOrThrow(error: () -> Throwable) = rightOrNull() ?: throw error()

    /**
     * Returns the value bound at the left type ([L]) or returns the default value
     * returned by the given [default] function.
     */
    fun leftOrDefault(default: () -> L) = leftOrNull() ?: default()

    /**
     * Returns the value bound at the right type ([R]) or returns the default value
     * returned by the given [default] function.
     */
    fun rightOrDefault(default: () -> R) = rightOrNull() ?: default()

    /**
     * Returns a Kotlin's [Result] representation from this [Either] binding.
     * The [right] side is often referred as the "error" or "failure" type in functional
     * languages or multiparadigm languages such as Rust.
     */
    fun asResult(): Result<L> {
        return Result.success(leftOrNull() ?: return Result.failure(BoundTypeMismatchException('R')))
    }

    /**
     * Apply the given [transform] function to the element bound on the [left] side
     * ([L]) if it is present.
     * @return A new [Either] type containing the result of this [mapLeft] call.
     */
    @OptIn(ExperimentalContracts::class)
    fun <T> mapLeft(transform: (L) -> T): Either<T, R> {
        contract {
            callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
        }
        return left(transform(left ?: return right(right ?: error("Both Either sides are bound to null."))))
    }

    /**
     * Apply the given [transform] function to the element bound on the [right] side
     * ([R]) if it is present.
     * @return A new [Either] type containing the result of this [mapRight] call.
     */
    @OptIn(ExperimentalContracts::class)
    fun <T> mapRight(transform: (R) -> T): Either<L, T> {
        contract {
            callsInPlace(transform, InvocationKind.EXACTLY_ONCE)
        }
        return right(transform(right ?: return left(left ?: error("Both Either sides are bound to null."))))
    }

    // overridden types
    override fun toString(): String = (left ?: right).toString()

    companion object {
        /**
         * Creates an [Either] type bound on the [left] side ([L]) with the given [value] and with
         * an empty [right] side ([R]).
         */
        fun <L, R> left(value: L): Either<L, R> = Either(left = value)

        /**
         * Creates an [Either] type bound to the [right] side ([R]) with the given [value] and with
         * an empty [left] side ([L]).
         */
        fun <L, R> right(value: R): Either<L, R> = Either(right = value)

        /**
         * Creates an [Either] type based on callback exception handling where the success output is
         * bound to the [left] side ([L]) and the error type is bound to the [right] side ([R]).
         */
        @OptIn(ExperimentalContracts::class)
        inline fun <L, reified R : Throwable> catching(block: () -> L): Either<L, R> {
            contract {
                callsInPlace(block, InvocationKind.EXACTLY_ONCE)
            }
            return try {
                left(block())
            } catch (exception: Throwable) {
                if (exception is R) {
                    right(exception)
                } else {
                    throw exception
                }
            }
        }

        /**
         * Creates an [Either] type bound at what side the given [value]'s type is.
         * @throws IllegalArgumentException if the given [value] does not match [L] and [R].
         */
        inline fun <reified L, reified R> decide(value: Any): Either<L, R> = if (value is L)
            left(value)
        else if (value is R)
            right(value)
        else
            throw IllegalArgumentException(
                "The provided value for Either#decide is not either '${L::class.simpleName}' or '${R::class.simpleName}'."
            )
    }

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    class Serializer<L, R>(private val leftSerializer: KSerializer<L>, private val rightSerializer: KSerializer<R>) :
        KSerializer<Either<L, R>> {
        override val descriptor: SerialDescriptor =
            buildSerialDescriptor("org.hexalite.network.common.util.Either", SerialKind.CONTEXTUAL)

        override fun serialize(encoder: Encoder, value: Either<L, R>) {
            if (value.isLeft()) {
                encoder.encodeNullableSerializableValue(leftSerializer, value.left)
            } else {
                encoder.encodeNullableSerializableValue(rightSerializer, value.right)
            }
        }

        override fun deserialize(decoder: Decoder): Either<L, R> {
            return try {
                left(decoder.decodeSerializableValue(leftSerializer))
            } catch (exception: SerializationException) {
                right(decoder.decodeSerializableValue(rightSerializer))
            }
        }
    }

    class BoundTypeMismatchException(side: Char) : NoSuchElementException("The Either type is not bound at [$side].")
}

