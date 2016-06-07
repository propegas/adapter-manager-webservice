package conf;

import com.google.inject.Inject;
import dao.SetupDao;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class StartupActions {

    private static final Logger logger = LoggerFactory.getLogger(StartupActions.class);
    
    @Inject
    SetupDao setupDao;

    private NinjaProperties ninjaProperties;

    @Inject
    public StartupActions(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }
    
    @Start(order=100)
    public void generateDummyDataWhenInTest() {
        
        if (!ninjaProperties.isProd()) {

            logger.debug("Ninja Dummy Data Setup Starting...");
            System.out.println("Ninja Dummy Data Setup Starting...");
            
            setupDao.setup();
            
        }
        
    }

}
