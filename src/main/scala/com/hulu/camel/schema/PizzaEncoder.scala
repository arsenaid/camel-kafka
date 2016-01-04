package com.hulu.camel.schema

import java.io.ByteArrayOutputStream

import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream}
import kafka.serializer.Encoder
import kafka.utils.VerifiableProperties

/**
 * Created by arsen.aydinyan on 1/4/16.
 */
class PizzaEncoder(props: VerifiableProperties = null) extends Encoder[Seq[Pizza]] {
  override def toBytes(pizzas: Seq[Pizza]): Array[Byte] = {
    val stream = new ByteArrayOutputStream
    val os = AvroOutputStream[Pizza](stream)
    os.write(pizzas)
    os.close
    stream.toByteArray
  }
}

object PizzaDecoder {
  def fromBytes(bytes: Array[Byte]): Seq[Pizza] = {
    val is = AvroInputStream[Pizza](bytes)
    val pizzaIterator: Iterator[Pizza] = is.iterator
    is.close
    pizzaIterator.toSeq
  }
}
