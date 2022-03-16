package com.yingenus.pocketchinese.functions.search

import androidx.work.ListenableWorker
import com.yingenus.pocketchinese.common.Result
import org.junit.Assert.*

import org.junit.Test
import java.lang.reflect.Executable

class UnitWordIteratorTest {

    @Test
    fun forEachRemaining() {

    }

    @Test
    fun intiClass() {
        try {
            val iterator = UnitWordIterator(MockUnitWordRepository())
            assert(true)
        }catch (e : Exception){
            assert(false)
        }
    }

    @Test
    fun intiClassWithError() {
        assertThrows( Exception::class.java){
            UnitWordIterator(MockUnitWordRepository().also { it.isFail = true })
        }
    }

    @Test
    fun hasNext() {
        val repo = MockUnitWordRepository()
        val iterator = UnitWordIterator(repo)

        for ( i in 1.rangeTo(MockUnitWordRepository.size)){
            assertTrue(iterator.hasNext())
            iterator.next()
        }
        assertFalse(iterator.hasNext())
    }

    @Test
    fun hasNextWithError() {
        val repo = MockUnitWordRepository()
        val iterator = UnitWordIterator(repo)
        repo.isFail = true
        try {
            iterator.hasNext()
            assert(true)
        }catch (e : Exception){
            assert(false)
        }
    }

    @Test
    fun next() {
        val repo = MockUnitWordRepository()
        val iterator = UnitWordIterator(repo)
        var count = 0

        while (iterator.hasNext()){
            val result = iterator.next()
            val expected = (repo.getUnitWord(++count) as Result.Success).value
            assertTrue( result.word == expected.word)
            assertTrue( result.unitWordId == expected.unitWordId)
        }
    }

    @Test
    fun nextWithError() {
        val repo = MockUnitWordRepository()
        val iterator = UnitWordIterator(repo)
        repo.isFail = true
        var count = 0
        assertThrows(java.lang.Exception::class.java){iterator.next()}
    }

    @Test
    fun nextWithErrorNotAll() {
        val repo = MockUnitWordRepository()
        val iterator = UnitWordIterator(repo)
        repo.isContainsAll = false
        try {
            while (iterator.hasNext()) iterator.next()
            assert(true)
        }catch (e : Exception){
            assert(false)
        }
    }
}