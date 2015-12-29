import com.hulu.camel.schema.{Ingredient, Pizza}
import com.sksamuel.avro4s.AvroSchema

val hawaiian = Pizza("hawaiian", Seq(Ingredient("ham", 1.5, 5.6), Ingredient("pineapple", 5.2, 0.2)), false, false, 91)

def toSchema(pizza: Pizza): String = {
  val schema = AvroSchema[Pizza]
  schema.toString(true)
}

toSchema(hawaiian)