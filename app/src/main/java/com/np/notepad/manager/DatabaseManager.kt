package com.np.notepad.manager

import com.np.notepad.model.NoteItem
import org.litepal.LitePal
import org.litepal.extension.delete
import org.litepal.extension.deleteAll
import org.litepal.extension.find
import org.litepal.extension.findAll

class DatabaseManager private constructor() {

  companion object {
    fun getInstance() = SingleHolder.INSTANCE
  }

  private object SingleHolder {
    val INSTANCE: DatabaseManager = DatabaseManager()
  }

  /**
   * save
   */
  fun log(text: String) {
    val item = NoteItem()
    item.title = "err"
    item.content = text
    item.save()
  }

  /**
   * get Topping size
   */
  fun getToppingSize(): Int = LitePal.findAll<NoteItem>().filter { it.top }.size

  /**
   * find all.
   */
  fun getAll(): MutableList<NoteItem> = LitePal.findAll<NoteItem>()

  fun getAllById(ids: LongArray): MutableList<NoteItem> {
    return LitePal.findAll(NoteItem::class.java, *ids)
  }

  /**
   * find by id
   */
  fun find(id: Long) = LitePal.find<NoteItem>(id)

  /**
   * delete by id
   */
  fun delete(id: Long) = LitePal.delete<NoteItem>(id)

  /**
   * delete by ids
   */
  fun delete(ids: List<String>) = LitePal.deleteAll<NoteItem>(
    "id IN (?)", ids.toString().substring(1, ids.toString().length - 1)
  )

  /**
   * save
   */
  fun save(model: NoteItem) = model.save()

  /**
   * update this id
   */
  fun update(model: NoteItem) = model.update(model.id)
}