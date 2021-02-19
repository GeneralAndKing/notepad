package com.np.notepad

import com.np.notepad.model.NoteItem
import org.junit.Test
import org.junit.Assert.*
import java.util.*

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
    print(list.toString().substring(1, list.toString().length - 1))
  }

  @Test
  fun save() {
    val note = NoteItem()
    note.title = "今天晚上要去吃小龙虾！"
    note.content = "配料：xxx想学习惺惺惜惺惺想嘻嘻嘻嘻嘻嘻嘻嘻寻寻寻学习嘻嘻嘻嘻嘻嘻嘻嘻嘻嘻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻寻"
    note.lastUpdateTime = Date()
    print(note.save())
  }
}
