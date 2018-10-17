package com.xmlcalabash.demo

import com.xmlcalabash.model.util.SaxonTreeBuilder
import com.xmlcalabash.runtime.{ExpressionContext, StaticContext, XProcMetadata, XmlPortSpecification}
import com.xmlcalabash.steps.DefaultXmlStep
import net.sf.saxon.s9api.{QName, XdmNode, XdmValue}

class ScalaTemplate extends DefaultXmlStep {
  private val _number = new QName("", "number")
  private var number = 0
  private var first: Option[Any] = None
  private var firstMeta: XProcMetadata = _
  private var second: Option[Any] = None
  private var secondMeta: XProcMetadata = _

  override def inputSpec: XmlPortSpecification = XmlPortSpecification.ANYSOURCESEQ
  override def outputSpec: XmlPortSpecification = XmlPortSpecification.ANYRESULT

  override def receiveBinding(variable: QName, value: XdmValue, context: ExpressionContext): Unit = {
    if (variable == _number) {
      number = value.getUnderlyingValue.head.getStringValue.toInt
    }
  }

  override def receive(port: String, item: Any, metadata: XProcMetadata): Unit = {
    if (second.isEmpty) {
      if (first.isEmpty) {
        first = Some(item)
        firstMeta = metadata
      } else {
        second = Some(item)
        secondMeta = metadata
      }
    }
  }

  override def run(context: StaticContext): Unit = {
    if (number % 2 == 0) {
      if (first.isDefined) {
        if (firstMeta.contentType.xmlContentType) {
          xmlResult(context, first.get.asInstanceOf[XdmNode])
        } else {
          consumer.get.receive("result", first.get, firstMeta)
        }
      }
    } else {
      if (second.isDefined) {
        if (secondMeta.contentType.xmlContentType) {
          xmlResult(context, second.get.asInstanceOf[XdmNode])
        } else {
          consumer.get.receive("result", second.get, secondMeta)
        }
      }
    }
  }

  private def xmlResult(context: StaticContext, doc: XdmNode): Unit = {
    val builder = new SaxonTreeBuilder(config)
    builder.startDocument(context.baseURI)
    builder.addStartElement(new QName("", "scala-template"))
    builder.addAttribute(new QName("", "number"), number.toString)
    builder.addAttribute(new QName("", "even"), (number % 2 == 0).toString)
    builder.startContent()
    builder.addText("\n")
    builder.addSubtree(doc)
    builder.addText("\n")
    builder.addEndElement()
    builder.endDocument()
    consumer.get.receive("result", builder.result, XProcMetadata.XML)
  }
}
