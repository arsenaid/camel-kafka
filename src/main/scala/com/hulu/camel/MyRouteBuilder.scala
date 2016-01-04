package com.hulu.camel

import java.util.UUID

import com.hulu.camel.schema.{PizzaDecoder, Ingredient, Pizza}
import kafka.message.DefaultCompressionCodec
import org.apache.camel.scala.dsl.builder.ScalaRouteBuilder
import org.apache.camel.{CamelContext, Exchange}

/**
 * A Camel Router using the Scala DSL
 */
class MyRouteBuilder(override val context: CamelContext) extends ScalaRouteBuilder(context) {

  val testTopic: String = UUID.randomUUID().toString
  val clientId: String = UUID.randomUUID().toString
  val producer = new KafkaProducer(testTopic,"192.168.99.100:9092")
  val codec = DefaultCompressionCodec.codec

  val pizzaBaker: Seq[Pizza] =  {
    val pepperoni = Pizza("pepperoni", Seq(Ingredient("pepperoni", 12, 4.4), Ingredient("onions", 1, 0.4)), false, false, 98)
    val hawaiian = Pizza("hawaiian", Seq(Ingredient("ham", 1.5, 5.6), Ingredient("pineapple", 5.2, 0.2)), false, false, 91)
    Array(pepperoni,hawaiian)
  }

  val pizzaByteUnfreezer = (exchange: Exchange) => {
    val pizzaStream: Array[Byte] = exchange.getIn.getBody(classOf[Array[Byte]])
    val pizzas: Seq[Pizza] = PizzaDecoder.fromBytes(pizzaStream)
    println("Unfrozen pizzas:" + pizzas.mkString("\n"))
    exchange.getIn.setBody(pizzas)
  }

  val pizzaByteFreezer = (exchange: Exchange) => {
    val pizzas: Seq[Pizza] = pizzaBaker
//    producer.send(pizzaStream, null)
    println("Frozen pizzas:" + pizzas.mkString("\n"))
    exchange.getIn.setBody(pizzas)
  }

  // a route using Scala blocks
  "timer://foo?period=5s" ==> {
    process(pizzaByteFreezer)
//    setHeader(KafkaConstants.PARTITION_KEY,"1")
//    setHeader(KafkaConstants.KEY,"1")
    to(s"kafka:?topic=${testTopic}&zookeeperConnect=192.168.99.100:2181&brokers=192.168.99.100:9092&requestRequiredAcks=-1" +
      s"&clientId=${clientId}&compressionCodec=${codec}&producerType=sync&serializerClass=com.hulu.camel.schema.PizzaEncoder")
    to("log:producer")
  }

  s"kafka:?topic=${testTopic}&zookeeperConnect=192.168.99.100:2181&brokers=192.168.99.100:9092&groupId=group1" ==> {
    process(pizzaByteUnfreezer)
    to("log:consumer")
  }
}