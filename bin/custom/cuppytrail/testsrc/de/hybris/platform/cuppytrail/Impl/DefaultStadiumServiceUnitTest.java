package de.hybris.platform.cuppytrail.Impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cuppytrail.daos.StadiumDAO;
import de.hybris.platform.cuppytrail.model.StadiumModel;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class DefaultStadiumServiceUnitTest {

    private DefaultStadiumService stadiumService;
    private StadiumDAO stadiumDAO;

    private StadiumModel stadiumModel;
    private static final String STADION_NAME = "wembley";
    private static final Integer STADION_CAPACITY = Integer.valueOf(100000);

    @Before
    public void setUp(){
        // We will be testing StadiumServiceImpl - an implementation of StadiumService
        stadiumService = new DefaultStadiumService();

        // So as not to implicitly also test the DAO, we will mock out the DAO using EasyMock
        stadiumDAO = mock(StadiumDAO.class);

        // and inject this mocked DAO into the StadiumService
        stadiumService.setStadiumDAO(stadiumDAO);

        // This instance of a StadiumModel will be used by the tests
        stadiumModel = new StadiumModel();
        stadiumModel.setCode(STADION_NAME);
        stadiumModel.setCapacity(STADION_CAPACITY);
    }

    /**
     * This test tests and demonstrates that the Service's getAllStadium method calls the DAOs' getAllStadium method and
     * returns the data it receives from it.
     */
    @Test
    public void testGetAllStadium(){
        // We construct the data we would like the mocked out DAO to return when called
        final List<StadiumModel> stadiumModels = Arrays.asList(stadiumModel);
        //Use Mockito and compare results
        when(stadiumDAO.findStadiums()).thenReturn(stadiumModels);
        // Now we make the call to StadiumService's getAllStadium which we expect to call the DAOs' getAllStadium method
        final List<StadiumModel> result = stadiumService.getStadiums();
        // We then verify that the results returned from the service are equals to those returned by the mocked out DAO
        assertEquals("We should find one", 1, result.size());
        assertEquals("And should equals what the mock returned", stadiumModel, result.get(0));
    }

    @Test
    public void testGetStadium(){
        // Tell Mockito we expect a call to the DAO's getStadium, and if it occurs Mockito should return stadiumModel
        when(stadiumDAO.findStadiumsByCode(STADION_NAME)).thenReturn(Collections.singletonList(stadiumModel));
        // We make the call to the Service's getStadium which we expect to call the DAO's getStadium method.
        final StadiumModel result = stadiumService.getStadiumForCode(STADION_NAME);
        // We then verify that the result returned form the Service is the same as that returned from the DAO
        assertEquals("And should equals what the mock returned", stadiumModel, result);
    }
}
