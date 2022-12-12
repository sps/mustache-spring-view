/*
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.servlet.view.mustache.jmustache;

import com.samskivert.mustache.Mustache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.view.mustache.MustacheTemplate;
import org.springframework.web.servlet.view.mustache.MustacheTemplateException;
import org.springframework.web.servlet.view.mustache.MustacheTemplateFactory;

import java.io.Reader;

/**
 * @author Sean Scanlon <sean.scanlon@gmail.com>
 */
public class JMustacheTemplateFactory implements MustacheTemplateFactory, InitializingBean {

    private JMustacheTemplateLoader templateLoader;
    private Mustache.Compiler compiler = null;

    private boolean standardsMode = false;
    private boolean escapeHTML = true;
    private String prefix = "";
    private String suffix = "";

    public MustacheTemplate getTemplate(String templateURL) throws MustacheTemplateException {
        try {
            final Reader templateReader = templateLoader.getTemplate(templateURL);
            return new JMustacheTemplate(compiler.compile(templateReader));
        } catch (Exception e) {
            throw new MustacheTemplateException(e);
        }
    }


    public void afterPropertiesSet() throws Exception {
        templateLoader.setPrefix(prefix);
        templateLoader.setSuffix(suffix);
        if (compiler == null) {
            compiler = Mustache.compiler()
                    .escapeHTML(escapeHTML)
                    .standardsMode(standardsMode)
                    .withLoader(templateLoader);
        }
    }

    // @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    // @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setTemplateLoader(JMustacheTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    /**
     * Whether or not standards mode is enabled.
     * <p/>
     * disabled by default.
     */
    public void setStandardsMode(boolean standardsMode) {
        this.standardsMode = standardsMode;
    }

    /**
     * Whether or not HTML entities are escaped by default.
     * <p/>
     * default is true.
     */
    public void setEscapeHTML(boolean escapeHTML) {
        this.escapeHTML = escapeHTML;
    }

    /**
     * You can inject your own custom configured compiler. If you don't inject one then a default one will be created
     * for you instead using the standardsMode, escapeHTML, and templateLoader values you've injected.
     *
     * @param compiler
     */
    public void setCompiler(Mustache.Compiler compiler) {
        this.compiler = compiler;
    }

}