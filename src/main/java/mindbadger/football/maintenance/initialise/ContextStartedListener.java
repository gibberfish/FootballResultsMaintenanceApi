package mindbadger.football.maintenance.initialise;

import mindbadger.football.maintenance.api.MappingCache;
import mindbadger.football.maintenance.util.ApplicationContextProvider;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;

@Component
public class ContextStartedListener implements ApplicationListener<ContextStartedEvent> {
    private Logger logger = Logger.getLogger(ContextStartedListener.class);

    @Override
    public void onApplicationEvent(ContextStartedEvent cse) {
        logger.debug("Handling context started event. ");

        MappingCache mappingCache = (MappingCache) ApplicationContextProvider.getApplicationContext().getBean("mappingCache");
        mappingCache.refreshCache();
    }
}
