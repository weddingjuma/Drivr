/*
 * Copyright 2016 CMPUT301F16T10
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package ca.ualberta.cs.drivr;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * An activity that shows information about a request.
 */
public class RequestActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView routeText;
    private TextView fareText;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Context mContext;
    private MapController mapController;
    private Place sourcePlace;
    private Place destinationPlace;
    private UserManager userManager = UserManager.getInstance();
    private User user = userManager.getUser();

    private TextView acceptButton;
    private TextView declineButton;

    private static final String TAG = "RequestActivity";
    /**
     * Used as a key for passing a request into this activity as an extra to an intent.
     */
    public static final String EXTRA_REQUEST = "ca.ualberta.cs.drivr.RequestActivity.EXTRA_REQUEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        routeText = (TextView) findViewById(R.id.request_route_text);
        fareText = (TextView) findViewById(R.id.request_fare_text);
        acceptButton = (TextView) findViewById(R.id.request_accept_text);
        declineButton = (TextView) findViewById(R.id.request_decline_text);

        // map = findViewById(R.id.request_map_fragment);

        String requestString = getIntent().getStringExtra(EXTRA_REQUEST);
//        Gson gson = new Gson();
//        ConcretePlace concretePlace = new ConcretePlace(place);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .create();
        final Request request = gson.fromJson(requestString, Request.class);
        sourcePlace = request.getSourcePlace();
        destinationPlace = request.getDestinationPlace();

        fareText.setText("$" + request.getFareString());
        routeText.setText(request.getRoute());

        // TODO make a map with these points and the route between them
        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.request_map_fragment);
        mapFragment.getMapAsync(this);


        mContext = getApplicationContext();
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                RequestState state = request.getRequestState();
                if (state.equals(RequestState.CREATED)) {
                    request.setRequestState(RequestState.PENDING);
                    RequestsListController requestsListController = new RequestsListController(UserManager.getInstance());
                    requestsListController.addRequest(request);
                    UserManager.getInstance().notifyObservers();
                    ElasticSearchController.AddRequest addRequest = new ElasticSearchController.AddRequest();
                    addRequest.execute(request);
                }
                RequestController requestController = new RequestController(userManager);
                if (requestController.canAcceptRequest(request, userManager.getUserMode())) {
                    Log.i(TAG, "attempting to accept request");
                    requestController.acceptRequest(request, getApplicationContext());
                }
                else if (requestController.canConfirmRequest(request, userManager.getUserMode())) {
                    requestController.confirmRequest(request);
                }
                else if (requestController.canCompleteRequest(request, userManager.getUserMode())) {
                    requestController.completeRequest(request);
                }
//                else if (state.equals(RequestState.PENDING) && UserManager.getInstance().getUserMode().equals(UserMode.DRIVER)) {
//                    request.addDriver((Driver) UserManager.getInstance().getUser());
////                    request.setRequestState(RequestState.ACCEPTED);
////                    ElasticSearchController.UpdatePendingRequest update = new ElasticSearchController.UpdatePendingRequest();
////                    update.execute(request);
//                }

                Log.i("Request State:", request.getRequestState().toString());
                finish();

//                RequestState state = request.getRequestState();
//                RequestState.
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestActivity.this, NewRequestActivity.class);

                Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriSerializer())
                        .create();

                String concretePlaceJsonPick = gson.toJson(sourcePlace);
                String concretePlaceJsonDest = gson.toJson(destinationPlace);

                intent.putExtra(NewRequestActivity.EXTRA_PLACE, concretePlaceJsonDest);
                Log.i(TAG, "Place: " + destinationPlace.getName() + ", :" + destinationPlace.getLatLng());

                intent.putExtra("PICK_UP", concretePlaceJsonPick);
                Log.i(TAG, "Place: " + sourcePlace.getName() + ", :" + sourcePlace.getLatLng());

                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        final MapController mapController = new MapController(map,mContext);
        if(sourcePlace != null && destinationPlace != null) {
            mapController.addRequestOnMap(sourcePlace.getLatLng(), destinationPlace.getLatLng());
            mapController.requestCenter(sourcePlace.getLatLng(),destinationPlace.getLatLng());
        }
    }
}
