[Mustache.js](http://mustache.github.com/mustache.5.html) View for [Spring Web MVC](http://static.springsource.org/spring/docs/4.0.x/spring-framework-reference/html/mvc.html)
============================================================================
Supports both [jmustache](https://github.com/samskivert/jmustache) and [mustache.java](https://github.com/spullara/mustache.java)

[![Build Status](https://travis-ci.org/sps/mustache-spring-view.png?branch=master)](https://travis-ci.org/sps/mustache-spring-view)
[![Coverage Status](https://coveralls.io/repos/sps/mustache-spring-view/badge.png?branch=master)](https://coveralls.io/r/sps/mustache-spring-view?branch=master)

Maven Dependency
-----------------

    <dependency>
        <groupId>com.github.sps.mustache</groupId>
        <artifactId>mustache-spring-view</artifactId>
        <version>1.3</version>
    </dependency>

    <!-- jmustache -->
    <dependency>
        <groupId>com.samskivert</groupId>
        <artifactId>jmustache</artifactId>
        <version>${jmustache.version}</version>
    </dependency>

    <!-- mustache.java -->
    <dependency>
		<groupId>com.github.spullara.mustache.java</groupId>
        <artifactId>compiler</artifactId>
        <version>${mustache.java.version}</version>
    </dependency>



Spring Configuration
-------------
    <!-- jmustache -->
    <bean id="viewResolver" class="org.springframework.web.servlet.view.mustache.MustacheViewResolver">
        <property name="suffix" value=""/>
        <property name="cache" value="${TEMPLATE_CACHE_ENABLED}" />
        <property name="templateFactory">
            <bean class="org.springframework.web.servlet.view.mustache.jmustache.JMustacheTemplateFactory">
                <property name="escapeHTML" value="true"/>
                <property name="standardsMode" value="false"/>
                <property name="templateLoader">
                    <bean class="org.springframework.web.servlet.view.mustache.jmustache.JMustacheTemplateLoader"/>                                
                </property>
            </bean>
        </property>
    </bean>

	<!-- mustache.java -->
    <bean id="viewResolver" class="org.springframework.web.servlet.view.mustache.MustacheViewResolver">
        <property name="suffix" value=""/>
        <property name="cache" value="${TEMPLATE_CACHE_ENABLED}"/>
        <property name="templateFactory">
            <bean class="org.springframework.web.servlet.view.mustache.java.MustacheJTemplateFactory" />
        </property>
    </bean>



Localization Support
---------------
	<bean id="messageSource" .../>
    
    <!-- using mustache.java -->
    <bean id="i18nMessageInterceptor"
          class="org.springframework.web.servlet.view.mustache.java.LocalizationMessageInterceptor">
        <property name="localeResolver" ref="..." />
    </bean>

	<!-- using jmustache -->
	<bean id="i18nMessageInterceptor" class="org.springframework.web.servlet.view.mustache.jmustache.LocalizationMessageInterceptor">
        <property name="localeResolver" ref="..." />
    </bean>

Spring Boot 3 - Configuration
-------------
Once you have a spring boot 3 web application running, register the mustache spring view dependency via the pom.xml:
```
<dependency>
    <groupId>com.github.sps.mustache</groupId>
    <artifactId>mustache-spring-view</artifactId>
    <version>1.5-SNAPSHOT</version>
</dependency>
```
Create the localization implementation for mustache i18n lambda
```
public class LocalizationMessageInterceptor extends MustacheLocalizationMessageInterceptor {
    public LocalizationMessageInterceptor() {
    }

    protected Object createHelper(final HttpServletRequest request) {
        return new Mustache.Lambda() {
            public void execute(Template.Fragment frag, Writer out) throws IOException {
                LocalizationMessageInterceptor.this.localize(request, frag.execute(), out);
            }
        };
    }
}
```
Register the localization implementation
```
@Component 
public class InterceptorRegistration  implements WebMvcConfigurer {
    @Autowired
    LocalizationMessageInterceptor localizationMessageInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localizationMessageInterceptor);
    }
}
```
Ensure to add your `messages.properties` files in your application.yaml:
```
spring:
    messages:
        basename: "messages.error.messages,messages.business.messages"
```
This means: having a directory `messages` containing `error` and `business` directory, having messages bundle within it (messages.properties messages_uk.
properties etc)


Thanks
---------------
Thanks to [Eric White](https://github.com/ericdwhite) for [forking](https://github.com/ericdwhite/mustache.java-spring-webmvc/) this code base and providing the mustache.java implementation.