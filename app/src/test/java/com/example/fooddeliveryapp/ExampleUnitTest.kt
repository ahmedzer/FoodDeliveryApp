package com.example.fooddeliveryapp

import com.example.fooddeliveryapp.DB.Dao.AppDataBase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {


    lateinit var appDB :AppDataBase

    @Before
    fun createDB() {

    }
    @Test
    fun addition_isCorrect() {

        assertEquals(4, 2 + 2)
    }
}