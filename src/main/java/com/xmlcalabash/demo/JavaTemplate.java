package com.xmlcalabash.demo;

import com.xmlcalabash.model.util.SaxonTreeBuilder;
import com.xmlcalabash.runtime.ExpressionContext;
import com.xmlcalabash.runtime.StaticContext;
import com.xmlcalabash.runtime.XProcMetadata;
import com.xmlcalabash.runtime.XmlPortSpecification;
import com.xmlcalabash.steps.DefaultXmlStep;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import net.sf.saxon.trans.XPathException;

public class JavaTemplate extends DefaultXmlStep {
    private final QName _number = new QName("", "number");
    private int number = 0;
    private Object first = null;
    private XProcMetadata firstMeta = null;
    private Object second = null;
    private XProcMetadata secondMeta = null;

    @Override
    public XmlPortSpecification inputSpec() {
        return XmlPortSpecification.ANYSOURCESEQ();
    }

    @Override
    public XmlPortSpecification outputSpec() {
        return XmlPortSpecification.ANYRESULT();
    }

    @Override
    public void receiveBinding(QName variable, XdmValue value, ExpressionContext context) {
        if (_number.equals(variable)) {
            try {
                number = Integer.parseInt(value.getUnderlyingValue().head().getStringValue());
            } catch (XPathException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void receive(String port, Object item, XProcMetadata metadata) {
        if (second == null) {
            if (first == null) {
                first = item;
                firstMeta = metadata;
            } else {
                second = item;
                secondMeta = metadata;
            }
        }
    }

    @Override
    public void run(StaticContext context) {
        if (number % 2 == 0) {
            if (first != null) {
                if (firstMeta.contentType().xmlContentType()) {
                    xmlResult(context, (XdmNode) first);
                } else {
                    consumer().get().receive("result", first, firstMeta);
                }
            }
        } else {
            if (second != null) {
                if (secondMeta.contentType().xmlContentType()) {
                    xmlResult(context, (XdmNode) second);
                } else {
                    consumer().get().receive("result", second, secondMeta);
                }
            }
        }
    }

    private void xmlResult(StaticContext context, XdmNode doc) {
        SaxonTreeBuilder builder = new SaxonTreeBuilder(config()); // FIXME: what's the deal with protected?
        builder.startDocument(context.baseURI());
        builder.addStartElement(new QName("", "java-template"));
        builder.addAttribute(new QName("", "number"), "" + number);
        builder.addAttribute(new QName("", "even"), Boolean.toString(number % 2 == 0));
        builder.startContent();
        builder.addText("\n");
        builder.addSubtree(doc);
        builder.addText("\n");
        builder.addEndElement();
        builder.endDocument();
        consumer().get().receive("result", builder.result(), XProcMetadata.XML());
    }
}
