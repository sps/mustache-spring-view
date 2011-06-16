[mustache.js](http://mustache.github.com/mustache.5.html) view for [spring3](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/mvc.html)
---------------------------

maven dependency
-----------------

    <dependency>
        <groupId>com.github.sps.mustache</groupId>
        <artifactId>mustache-spring-view</artifactId>
        <version>1.0</version>
    </dependency>


spring config
-------------

    <bean id="viewResolver" class="org.springframework.web.servlet.view.mustache.MustacheViewResolver">
        <property name="cache" value="${TEMPLATE_CACHE_ENABLED}" />
        <!-- do not throw exception when encouter null-->
        <property name="nullValue" value=""/>
        <property name="prefix" value="" />
        <property name="suffix" value=".html" />
        <property name="templateLoader">
            <property name="templateLoader">
                <bean class="org.springframework.web.servlet.view.mustache.MustacheTemplateLoader">
                    <!--encoding of template-->
                    <property name="encoding" value="utf-8"/>
                </bean>
            </property>
        </property>
        <property name="contentType">
            <!-- same with encoding of template-->
            <value>text/html; charset=utf-8</value>
        </property>
    </bean>
