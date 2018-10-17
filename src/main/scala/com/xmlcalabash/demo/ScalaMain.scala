package com.xmlcalabash.demo

import java.net.URI

import com.xmlcalabash.config.XMLCalabashConfig
import com.xmlcalabash.runtime.{PrintingConsumer, XMLCalabashRuntime}
import net.sf.saxon.s9api.QName

object ScalaMain extends App {
  // Compute these first so that number format exceptions and other problems
  // cause the program to crash before we initialize the runtime object.
  // See the alternative approach in JavaMain.

  val opt = if (args.length > 1) {
    Some(args(1).toInt)
  } else {
    None
  }

  val version = if (args.length > 2) {
    Some(args(2))
  } else {
    None
  }

  val config = XMLCalabashConfig.newInstance()
  val runtime: XMLCalabashRuntime = config.runtime(new URI(args.head))

  if (opt.isDefined) {
    runtime.option(new QName("","opt"), opt.get)
  }

  if (version.isDefined) {
    runtime.option(new QName("","version"), version.get)
  }

  val serOpt = runtime.serializationOptions("result")
  val pc = new PrintingConsumer(runtime, serOpt)
  runtime.output("result", pc)

  // If running the pipeline throws an exception, stop() will be called automatically
  // so we don't have to worry about that here.
  runtime.run()
}
