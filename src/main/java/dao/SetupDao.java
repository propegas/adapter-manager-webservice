package dao;


import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.Adapter;
import models.AdapterConfigFile;
import models.UserAuth;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import java.util.List;

public class SetupDao {
    
    @Inject
    Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void setup() {
        
        EntityManager entityManager = entityManagerProvider.get();
        
        Query q = entityManager.createQuery("SELECT x FROM UserAuth x");
        List<UserAuth> userAuths = (List<UserAuth>) q.getResultList();
        
        if (userAuths.size() == 0) {

            // Create a new user and save it
            UserAuth bob = new UserAuth("bob@gmail.com", "secret", "Bob");
            entityManager.persist(bob);
            
            // Create a new adapter
            Adapter bobPost3 = new Adapter("Zabbix Events Adapter",
                    "Адаптер для сбора событий Zabbix.",
                    "camelZabbix.jar");
            bobPost3.setCheckStatusCommands("ps -ef | grep -i camelZabbix.jar | grep -v grep");
            bobPost3.setStatus("Unknown");
            bobPost3.setJarFilePath("/home/user007/zsm/adapters/camelZabbix2.0/");
            bobPost3.setStartCommands(
                    "cd /home/user007/zsm/adapters/\n" +
                    "./ZabbixEventsAdapter.sh start"
            );
            bobPost3.setStopCommands(
                    "cd /home/user007/zsm/adapters/\n" +
                     "./ZabbixEventsAdapter.sh stop"
            );
            entityManager.persist(bobPost3);

            // Create config file
            System.out.println("[***TEST***] Adapter Id: " + bobPost3.id);
            AdapterConfigFile testConfFile = new AdapterConfigFile(bobPost3.id, "/home/file.properties");
            testConfFile.setConfigDescription("Test тест ЙЦУЙЦУ Zabbix.");
            entityManager.persist(testConfFile);

            entityManager.setFlushMode(FlushModeType.COMMIT);
            entityManager.flush();
        }

    }
    
    String lipsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit sed nisl sed lorem commodo elementum in a leo. Aliquam erat volutpat. Nulla libero odio, consectetur eget rutrum ac, varius vitae orci. Suspendisse facilisis tempus elit, facilisis ultricies massa condimentum in. Aenean id felis libero. Quisque nisl eros, accumsan eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula. eget ornare id, pharetra eget felis. Aenean purus erat, egestas nec scelerisque non, eleifend id ligula.";

    
    public String post1Title = "Hello to the blog example!";
    public String post1Content = 
            "<p>Hi and welcome to the demo of Ninja!</p> "
            + "<p>This example shows how you can use Ninja in the wild. Some things you can learn:</p>"
            + "<ul>"
            + "<li>How to use the templating system (header, footer)</li>"
            + "<li>How to test your application with ease.</li>"
            + "<li>Setting up authentication (login / logout)</li>"
            + "<li>Internationalization (i18n)</li>" 
            + "<li>Static assets / using webjars</li>"
            + "<li>Persisting data</li>"
            + "<li>Beautiful <a href=\"/adapter/3\">html routes</a> for your application</li>"
            + "<li>How to design your restful Api (<a href=\"/api/bob@gmail.com/adapters.json\">Json</a> and <a href=\"/api/bob@gmail.com/adapters.xml\">Xml</a>)</li>"
            + "<li>... and much much more.</li>"
            + "</ul>" 
            + "<p>We are always happy to see you on our mailing list! "
            + "Check out <a href=\"http://www.ninjaframework.org\">our website for more</a>.</p>";

}
