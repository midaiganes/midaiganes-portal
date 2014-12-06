package ee.midaiganes.portlets.webcontent;

import javax.portlet.Portlet;

import com.google.inject.AbstractModule;

import ee.midaiganes.portletsservices.webcontent.WebContentRepository;

public class WebContentModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Portlet.class).to(WebContentPortlet.class);
        bind(WebContentRepository.class);
        bind(WebContentEditController.class);
        bind(WebContentController.class);
    }
}
