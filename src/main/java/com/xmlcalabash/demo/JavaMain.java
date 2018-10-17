package com.xmlcalabash.demo;

import com.xmlcalabash.config.XMLCalabashConfig;
import com.xmlcalabash.runtime.PrintingConsumer;
import com.xmlcalabash.runtime.XMLCalabashRuntime;
import com.xmlcalabash.util.SerializationOptions;
import net.sf.saxon.s9api.QName;

import java.net.URI;

public class JavaMain {
    public static void main(String[] args) throws Exception {
        XMLCalabashConfig config = XMLCalabashConfig.newInstance();
        XMLCalabashRuntime runtime = config.runtime(new URI(args[0]));

        try {
            if (args.length > 1) {
                runtime.option(new QName("", "opt"), Integer.parseInt(args[1]));
            }

            if (args.length > 2) {
                runtime.option(new QName("", "version"), args[2]);
            }

            SerializationOptions serOpt = runtime.serializationOptions("result");
            PrintingConsumer pc = new PrintingConsumer(runtime, serOpt);
            runtime.output("result", pc);
        } catch (Exception ex) {
            // Once we've constructed a runtime, there are actors in the wings.
            // If this thread crashes, the program will still be waiting for
            // those other threads to end. Make sure we call stop() to terminate them.
            // See the alternative approach in ScalaMain.
            runtime.stop();
            throw ex;
        }

        // If running the pipeline throws an exception, stop() will be called automatically
        // so we don't have to worry about that here.
        runtime.run();
    }
}
