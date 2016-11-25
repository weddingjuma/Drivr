package ca.ualberta.cs.drivr;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by colton on 2016-11-23.
 */

public class SearchRequest {
    private String minPrice;
    private String maxPrice;
    private String minPricePer;
    private String maxPricePer;
    private ArrayList<Request> requestList;

    private ConcretePlace location;
    private String keyword;

    public SearchRequest(String minPrice, String maxPrice, String minPricePer, String maxPricePer, ConcretePlace location, String keyword) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minPricePer = minPricePer;
        this.maxPricePer = maxPricePer;
        this.location = location;
        this.keyword = keyword;
        requestList = new ArrayList<Request>();
    }

    //TODO: When searches are in place just remove the return null
    public ArrayList<Request> getRequests(){
        //return requestList;
        return null;
    }

    /**
     * Reason for Location split (just temporary, remove this when done):
     *
     * US 04.05.01 (added 2016-11-14)
     * As a driver, I should be able to search **by address** or **nearby an address**.
     *
     * One will use the ES SearchForLocationRequests to get all sourceAddresses with that location
     *
     * One will convert the location to geolocation and use ES SearchForGeolocationRequests to get
     * all requests around that location
     */
    private void SearchKeyword() {
        throw new UnsupportedOperationException();
    }
    private void SearchExactLocation() {
        throw new UnsupportedOperationException();
    }
    //TODO: Get geolocation for location, use that in ElasticSearch SearchForGeolocationRequests
    private void SearchNearLocation() {
        throw new UnsupportedOperationException();
    }

    //Price filters are handled here (makes more sense)
    private void FilterByPrice() {
        throw new UnsupportedOperationException();
    }
    private void FilterByPricePer() {
        throw new UnsupportedOperationException();
    }
}
