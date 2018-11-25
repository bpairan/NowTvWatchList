package com.sky.nowtv.watchlist

import com.google.inject.AbstractModule
import com.sky.nowtv.watchlist.common.{Content, Customer}
import net.codingwell.scalaguice.ScalaModule

import scala.collection.mutable

class Module extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[mutable.MultiMap[Customer,Content]].toInstance(new mutable.HashMap[Customer, mutable.Set[Content]]() with mutable.MultiMap[Customer,Content])
  }

}
