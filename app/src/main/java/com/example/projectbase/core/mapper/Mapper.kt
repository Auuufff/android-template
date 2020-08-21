package com.projectphenom.android.core.mapper

/**
 * Parent class for any Mapper.
 */
interface Mapper<S, D> {

    fun mapFromObject(source: S): D

    fun mapFromObjects(sources: Collection<S>) = sources.map(::mapFromObject)
}