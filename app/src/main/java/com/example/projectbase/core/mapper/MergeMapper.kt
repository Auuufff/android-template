package com.example.projectbase.core.mapper

/**
 * Parent class for any MergeMapper.
 */
interface MergeMapper<S, A, R> {
    fun mapFromObject(source: S, merge: A): R
    fun mapFromObjects(sources: Collection<S>, merges: Collection<A>): List<R> {
        return sources.zip(merges) { source, merge ->
            mapFromObject(source, merge)
        }
    }
}