package com.np.notepad

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun test() {
      val list = emptyList<Long>().toMutableList()
      list.add(2)
      list.add(4)
      list.add(5)
      list.add(6)
      list.add(7)
      print(list.toString().substring(1,list.toString().length-1))
  }
}
