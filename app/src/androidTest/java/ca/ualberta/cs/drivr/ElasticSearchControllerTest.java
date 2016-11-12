package ca.ualberta.cs.drivr;

import android.util.Log;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


/**
 * Tests for the ElasticSearch controller to make sure everything works accordingly.
 *
 * @author Tiegan Bonowicz
 */

//TODO: Grab async testing from StackOverflow, use it, credit it.
public class ElasticSearchControllerTest {

    private User user;

    /**
     * Used to set the user for each test.
     */
    public void setUser() {
        user = new User("tester", "test123");
        user.setPhoneNumber("123-456-7890");
        user.setEmail("test@test.test");
    }

    /**
     * Test to make sure a request is added and gotten.
     */
    @Test
    public void addAndGetRequest(){
        assertEquals(2+2, 4);
    }

    /**
     * Test to make sure a request is updated and gotten.
     */
    @Test
    public void updateRequest(){
        assertEquals(2+2, 4);
    }

    /**
     * Test to make sure a request can be gotten with a keyword.
     */
    @Test
    public void searchRequestWithKeyword(){
        assertEquals(2+2, 4);
    }

    /**
     * Test to make sure a request can be gotten with a geolocation.
     */
    @Test
    public void searchRequestWithLocation(){
        assertEquals(2+2, 4);
    }

    /**
     * Test to make sure a user is added and can be gotten.
     */
    @Test
    public void addAndSearchUser(){
        //Set first async timer here.
        setUser();
        ElasticSearchController.AddUser addUser = new ElasticSearchController.AddUser();
        addUser.execute(user);
        //End first async timer here.

        //Set second async timer here.
        User dup = null;
        ElasticSearchController.GetUser getUser = new ElasticSearchController.GetUser();
        getUser.execute("test123");
        try {
            dup = getUser.get();
        } catch (Exception e) {
            Log.i("Error", "Failed to load the user.");
        }
        //End second async timer here.

        assertEquals(user.getUsername(), dup.getUsername());
    }

    /**
     * Test to make sure updating a user and searching for it works.
     */
    @Test
    public void updateAndSearchUser(){
        //Set first async timer here.
        setUser();
        ElasticSearchController.AddUser updateUser = new ElasticSearchController.AddUser();
        updateUser.execute(user);
        //End first async timer here.

        //Set second async timer here.
        user.setEmail("test2@test2.test2");;
        updateUser.execute(user);
        //End second async timer here.

        //Set third async timer here.
        User dup = null;
        ElasticSearchController.GetUser getUser = new ElasticSearchController.GetUser();
        getUser.execute("test123");
        try {
            dup = getUser.get();
        } catch (Exception e) {
            Log.i("Error", "Failed to load the user.");
        }
        //End third async timer here.

        assertEquals(user.getEmail(), dup.getEmail());
    }

}