[mustache.js](http://mustache.github.com/mustache.5.html) view for [spring3](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/mvc.html)
---------------------------

[![Build Status](https://travis-ci.org/sps/mustache-spring-view.png?branch=master)](https://travis-ci.org/sps/mustache-spring-view)

maven dependency
-----------------

    <dependency>
        <groupId>com.github.sps.mustache</groupId>
        <artifactId>mustache-spring-view</artifactId>
        <version>1.1.2</version>
    </dependency>


spring config
-------------

    <bean id="viewResolver" class="org.springframework.web.servlet.view.mustache.MustacheViewResolver">
        <property name="cache" value="${TEMPLATE_CACHE_ENABLED}" />
        <property name="prefix" value="" />
        <property name="suffix" value=".html" />
        <property name="templateLoader">
            <bean class="org.springframework.web.servlet.view.mustache.MustacheTemplateLoader"" />
        </property>
    </bean>

messages config
---------------

    <bean id="messageInterceptor" class="org.springframework.web.servlet.i18n.MustacheMessageInterceptor">
        <property name="messageSource" ref="messageSource" />
        <property name="localeResolver" ref="localeResolver" />
        <!--<property name="messageKey" value="i18n"/> default is 'i18n'-->
        <!--<property name="viewResolver" ref="viewResolver"/> normally @Autowired-->
    </bean>

## Optionally Inject Custom Mustache.Compiler

spring config with custom compiler
----------------------------------

    <bean id="viewResolver" class="org.springframework.web.servlet.view.mustache.MustacheViewResolver">
        <property name="cache" value="${TEMPLATE_CACHE_ENABLED}" />
        <property name="prefix" value="" />
        <property name="suffix" value=".html" />
        <property name="templateLoader" ref="templateLoader"/>
        <property name="compiler">
            <bean class="your.package.name.CustomMustacheCompiler" factory-method="yourFactoryMethodName">
                <constructor-arg ref="templateLoader"/>
            </bean>
        </property>
    </bean>

custom compiler
---------------
    public class CustomMustacheCompiler {
        private CustomMustacheCompiler() {}

        public static Mustache.Compiler yourFactoryMethodName(MustacheTemplateLoader templateLoader) {
            // customize your compiler as needed
            return Mustache.compiler()
                    .escapeHTML(true)
                    .standardsMode(false)
                    .defaultValue("")
                    .withLoader(templateLoader);
        }
    }
