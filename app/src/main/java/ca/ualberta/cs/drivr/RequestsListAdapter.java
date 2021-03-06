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

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

/**
 * A list adapter for rendering requests in a requests list recycler view.
 *
 * This is modified from:
 * <ul>
 *     <li>From: https://guides.codepath.com/android/using-the-recyclerview</li>
 *     <li>Author: CodePath</li>
 *     <li>Date accessed: November 10, 2016</li>
 * </ul>
 */
public class RequestsListAdapter extends RecyclerView.Adapter<RequestsListAdapter.ViewHolder> {

    /**
     * The context ot display the data.
     */
    private final Context context;

    /**
     * All requests.
     */
    private final ArrayList<Request> requests;

    /**
     * The requests to display.
     */
    private final ArrayList<Request> requestsToDisplay;

    /**
     * The user manager.
     */
    private final UserManager userManager;

    /**
     * A class for storing subviews of a view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // References to the views in the list item
        public final TextView otherUserNameTextView;
        public final TextView descriptionTextView;
        public final TextView fareTextView;
        public final TextView routeTextView;
        public final TextView statusTextView;
        public final ImageView callImageView;
        public final ImageView emailImageView;
        public final ImageView checkMarkImageView;
        public final ImageView xMarkImageView;

        /**
         * Instantiate a new ViewHolder.
         * @param itemView The view of the lsit item.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            otherUserNameTextView = (TextView) itemView.findViewById(R.id.item_request_other_user_name);
            descriptionTextView = (TextView) itemView.findViewById(R.id.item_request_description_text);
            fareTextView = (TextView) itemView.findViewById(R.id.item_request_fare_text);
            routeTextView = (TextView) itemView.findViewById(R.id.item_request_route_text);
            statusTextView = (TextView) itemView.findViewById(R.id.item_request_status_text);
            callImageView = (ImageView) itemView.findViewById(R.id.item_request_call_image);
            emailImageView = (ImageView) itemView.findViewById(R.id.item_request_email_image);
            checkMarkImageView = (ImageView) itemView.findViewById(R.id.item_request_complete);
            xMarkImageView = (ImageView) itemView.findViewById(R.id.item_request_deleted);
        }
    }

    /**
     * Instantiate a new RequestListAdapter.
     * @param context The context to display the the requests
     * @param requests The requests
     */
    public RequestsListAdapter(Context context, ArrayList<Request> requests, UserManager userManager) {
        this.context = context;
        this.requests = requests;
        this.requestsToDisplay = new ArrayList<>(requests);
        this.userManager = userManager;
    }

    /**
     * Inflates the view.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RequestsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View requestView = inflater.inflate(R.layout.item_request, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(requestView);
        return viewHolder;
    }

    public void filter(RequestState... statesArray) {
        ArrayList<RequestState> states = new ArrayList<>(Arrays.asList(statesArray));
        requestsToDisplay.clear();
        for (Request request : requests) {
            if (states.contains(request.getRequestState()))
                requestsToDisplay.add(request);
        }
        notifyDataSetChanged();
    }

    /**
     * Called when the view holder is wants to bind the request at a certain position in the list.
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RequestsListAdapter.ViewHolder viewHolder, final int position) {
        final Request request = requestsToDisplay.get(position);

        // Get the views to update
        final TextView otherUserNameTextView = viewHolder.otherUserNameTextView;
        final TextView descriptionTextView = viewHolder.descriptionTextView;
        final TextView fareTextView = viewHolder.fareTextView;
        final TextView routeTextView = viewHolder.routeTextView;
        final TextView statusTextView = viewHolder.statusTextView;
        final ImageView callImageView = viewHolder.callImageView;
        final ImageView emailImageView = viewHolder.emailImageView;
        final ImageView checkImageView = viewHolder.checkMarkImageView;
        final ImageView deleteImageView = viewHolder.xMarkImageView;

        // Todo Hide Image Views until correct Request State
        if(request.getRequestState() != RequestState.CONFIRMED) {
           checkImageView.setVisibility(View.INVISIBLE);
        }

        if(request.getRequestState() != RequestState.PENDING) {
           deleteImageView.setVisibility(View.INVISIBLE);
        }

        // Show the other person's name
        final DriversList drivers = request.getDrivers();


        // Get the username of the other user
        if (userManager.getUserMode() == UserMode.RIDER) {
            final String multipleDrivers = "Multiple Drivers Accepted";
            final String driverUsername = drivers.size() == 1 ? drivers.get(0).getUsername(): "No Driver Yet";
            otherUserNameTextView.setText(drivers.size() > 1 ? multipleDrivers : driverUsername);
        }
        else {
            otherUserNameTextView.setText(request.getRider().getUsername());
        }


        // If the request has a description, show it. Otherwise, hide te description
        if (Strings.isNullOrEmpty(request.getDescription()))
            descriptionTextView.setVisibility(View.GONE);
        else
            descriptionTextView.setText(request.getDescription());

        // Show the fare
        fareTextView.setText("$" + request.getFareString());

        // Show the route
        routeTextView.setText(request.getRoute());

        // Driver User
        otherUserNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String otherUsername = otherUserNameTextView.getText().toString();
                // there exists drivers
                if(otherUsername != "No Driver Yet") {
                    if(otherUsername != "Multiple Drivers Accepted") {
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(Uri.class, new UriSerializer())
                                .create();


                        ElasticSearch elasticSearch = new ElasticSearch(UserManager.getInstance().getConnectivityManager());
                        User user = elasticSearch.loadUser(otherUsername);

                        String driverString = gson.toJson(user, User.class);
                        Intent intent = new Intent(context, DriverProfileActivity.class);
                        intent.putExtra(DriverProfileActivity.DRIVER, driverString);
                        context.startActivity(intent);
                    }
                    else {
                        startMultipleDriverIntent(request);
                    }
                }
            }
        });

        routeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Uri.class, new UriSerializer())
                        .create();
                String requestString = gson.toJson(request, Request.class);
                Intent intent = new Intent(context, RequestActivity.class);
                intent.putExtra("UniqueID","From_RequestListActivity");
                intent.putExtra(RequestActivity.EXTRA_REQUEST, requestString);
                context.startActivity(intent);
            }
        });

        // Show the status text
        statusTextView.setText(request.getRequestState().toString());

        // Add a listener to the call image
        callImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drivers.size() == 0) {
                    Toast.makeText(context, "No driver number available at this time", Toast.LENGTH_SHORT).show();

                }
                // Start Dialer
                else if (drivers.size() == 1) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    String number;
                    if (UserManager.getInstance().getUserMode().equals(UserMode.RIDER)) {
                        number = drivers.get(0).getPhoneNumber();
                    }
                    else {
                        number = request.getRider().getPhoneNumber();
                    }
                    number = "tel:" + number;
                    intent.setData(Uri.parse(number));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    context.startActivity(intent);

                }
                else {

                    startMultipleDriverIntent(request);
                }
            }
        });

        // Add a listener to the email image
        emailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drivers.size() == 0) {
                    Toast.makeText(context, "No driver email available at this time", Toast.LENGTH_SHORT).show();

                }
                //http://stackoverflow.com/questions/8701634/send-email-intent
                else if(drivers.size() == 1) {

                    Intent intent = new Intent();
                    ComponentName emailApp = intent.resolveActivity(context.getPackageManager());
                    ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
                    boolean hasEmailApp = emailApp != null && !emailApp.equals(unsupportedAction);
                    String email;

                    if (UserManager.getInstance().getUserMode().equals(UserMode.RIDER)) {
                        email = drivers.get(0).getEmail();
                    }
                    else {
                        email = request.getRider().getEmail();
                    }
                    String subject = "Drivr Request: " + request.getId();
                    String body = "Drivr user " + drivers.get(0).getUsername();

                    if (hasEmailApp) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                        context.startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
                    }
                    else {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto",email, null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                }
                else {
                    startMultipleDriverIntent(request);

                }
            }
        });

        // Complete The Request
        checkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RequestCompletedActivity.class);
                intent.putExtra(RequestCompletedActivity.REQUEST_ID_EXTRA, request.getId());
                context.startActivity(intent);
            }
        });

        deleteImageView.setOnClickListener(new View.OnClickListener() {

            // Todo Delete the Request
            @Override
            public void onClick(View v) {
                v.getContext();
                ElasticSearch elasticSearch = new ElasticSearch((ConnectivityManager)v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
                elasticSearch.deleteRequest(request.getId());
                UserManager userManager = UserManager.getInstance();
                userManager.getRequestsList().removeById(request);
                userManager.notifyObservers();
                requestsToDisplay.remove(request);
                notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
    }

    /**
     * Get the number of items in the request list.
     * @return
     */
    @Override
    public int getItemCount() {
        return requestsToDisplay.size();
    }

    public void startMultipleDriverIntent(Request request){
        Intent intent = new Intent(context, DisplayDriverListActivity.class);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .create();
        String requestString = gson.toJson(request, Request.class);
        intent.putExtra("REQUEST", requestString);
        context.startActivity(intent);

    }
}
