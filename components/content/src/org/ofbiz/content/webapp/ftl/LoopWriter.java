package org.ofbiz.content.webapp.ftl;

import java.io.*;
import freemarker.template.TransformControl;
import freemarker.template.TemplateModelException;
import org.ofbiz.base.util.Debug;

public class LoopWriter extends Writer implements TransformControl {

    public LoopWriter(Writer out) {
    }

    public int onStart() throws TemplateModelException, IOException {  
        return TransformControl.EVALUATE_BODY;
    }

    public int afterBody() throws TemplateModelException, IOException {  
        return TransformControl.END_EVALUATION;
    }

    public void onError(Throwable t) throws Throwable {
        throw t;
    }

    public void close() throws IOException {  
    }

    public void write(char cbuf[], int off, int len) {
    }

    public void flush() throws IOException {
    }

}
