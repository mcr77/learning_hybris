package de.hybris.platform.cuppytrail.daos;

import de.hybris.platform.cuppytrail.model.StadiumModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class DefaultStadiumDAOIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private StadiumDAO stadiumDAO;

    @Resource
    private ModelService modelService;

    public static final String STADIUM_NAME = "wembley";

    public static final Integer STAIUM_CAPACITY = Integer.valueOf(50000);

    @Test
    public void stadiumDAOTest(){

        List<StadiumModel> stadiumsByCode = stadiumDAO.findStadiumsByCode(STADIUM_NAME);
        assertTrue("NO Stadium should be returned", stadiumsByCode.isEmpty());

        List<StadiumModel> allStadiums = stadiumDAO.findStadiums();
        final int size = allStadiums.size();

        final StadiumModel stadiumModel = new StadiumModel();
        stadiumModel.setCode(STADIUM_NAME);
        stadiumModel.setCapacity(STAIUM_CAPACITY);
        modelService.save(stadiumModel);

        allStadiums = stadiumDAO.findStadiums();
        assertEquals(size + 1, allStadiums.size());
        assertEquals("Unexpected Stadium found", stadiumModel, allStadiums.get(allStadiums.size() - 1));

        stadiumsByCode = stadiumDAO.findStadiumsByCode(STADIUM_NAME);
        assertEquals("Find the one we just saved", 1, stadiumsByCode.size());
        assertEquals("Check the names", STADIUM_NAME, stadiumsByCode.get(0).getCode());
        assertEquals("Check the capacity", STAIUM_CAPACITY, stadiumsByCode.get(0).getCapacity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void stadiumDAOCornerCases()
    {
        // Calling findStadiumsByCode with the empty string returns no results
        List<StadiumModel> stadiums = stadiumDAO.findStadiumsByCode("");
        assertTrue("No Stadium should be returned", stadiums.isEmpty());
        // Calling findStadiumByCode with null throws a IllegalArgumentException
        stadiums = stadiumDAO.findStadiumsByCode(null);

        // Create a StadiumModel and call saveStadiumModel
        final StadiumModel stadiumModel = new StadiumModel();
        stadiumModel.setCapacity(Integer.valueOf(1000));
        stadiumModel.setCode(STADIUM_NAME);
        modelService.save(stadiumModel);
    }
}
