package com.hulu.camel.schema

import java.io.{ByteArrayOutputStream, File}

import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream}

/**
 * Created by arsen.aydinyan on 12/23/15.
 */
object AvroSerializer {

  def serializePizzaToFile(pizzas: Seq[Pizza], pizzasOut:String): Unit = {
    val os = AvroOutputStream[Pizza](new File(s"${pizzasOut}.avro"))
    os.write(pizzas)
    os.close
  }

  def serializePizzasToBytes(pizzas: Seq[Pizza]): Array[Byte] = {
    val stream = new ByteArrayOutputStream
    val os = AvroOutputStream[Pizza](stream)
    os.write(pizzas)
    os.close
    stream.toByteArray
  }

  def deserializePizzasFromBytes(pizzas: Array[Byte]): Seq[Pizza] = {
    val is = AvroInputStream[Pizza](pizzas)
    val pizzaIterator: Iterator[Pizza] = is.iterator
    is.close
    pizzaIterator.toSeq
  }

}
