package camel;

import com.google.inject.Inject;
import dao.AdapterDao;
import dao.AdapterEventDao;

import javax.inject.Singleton;

/**
 * Created by vgoryachev on 06.06.2016.
 * Package: camel.
 */
@Singleton
public class TestTest {

    @Inject
    AdapterEventDao adapterEventDao;

    @Inject
    AdapterDao adapterDao;


}
