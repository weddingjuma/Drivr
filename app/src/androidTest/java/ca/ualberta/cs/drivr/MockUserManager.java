package ca.ualberta.cs.drivr;

/**
 * Created by adam on 2016-11-10.
 */

public class MockUserManager implements IUserManager {

    private User user;
    private MapUnits mapUnits;
    private UserMode userMode;
    private RequestsList requestsList;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public MapUnits getMapUnits() { return mapUnits; }
    public void setMapUnits(MapUnits mapUnits) { this.mapUnits = mapUnits; }
    public RequestsList getRequestsList() { return requestsList; }
    public void setRequestsList(RequestsList requestsList) { this.requestsList = requestsList; }
    public UserMode getUserMode() { return userMode; }
    public void setUserMode(UserMode userMode) { this.userMode = userMode; }
}
