package com.hulu.camel.schema

/**
 * Created by arsen.aydinyan on 12/23/15.
 */
case class Pizza(name: String, ingredients: Seq[Ingredient], vegetarian: Boolean, vegan: Boolean, calories: Int) {

}
