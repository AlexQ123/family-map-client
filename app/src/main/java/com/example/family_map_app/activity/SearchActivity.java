package com.example.family_map_app.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import model.*;

import com.example.family_map_app.R;
import com.example.family_map_app.serverdata.DataCache;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;

    private EditText searchText;
    private RecyclerView searchRecyclerView;

    private ArrayList<Person> personsToDisplay;
    private ArrayList<Event> eventsToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchText = findViewById(R.id.searchField);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString().toLowerCase();
                displaySearchResults(input);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private void displaySearchResults(String input) {
        personsToDisplay = new ArrayList<>();
        eventsToDisplay = new ArrayList<>();

        DataCache dataCache = DataCache.getInstance();
        ArrayList<Person> persons = dataCache.getPersons();
        ArrayList<Event> events = dataCache.getEvents();

        for (Person person : persons) {
            if (person.getFirstName().toLowerCase().contains(input) ||
            person.getLastName().toLowerCase().contains(input)) {
                personsToDisplay.add(person);
            }
        }

        for (Event event : events) {
            if (event.getCountry().toLowerCase().contains(input) ||
            event.getCity().toLowerCase().contains(input) ||
            event.getEventType().toLowerCase().contains(input)) {
                eventsToDisplay.add(event);
            }
        }

        if (input.length() == 0) {
            personsToDisplay.clear();
            eventsToDisplay.clear();
        }

        SearchAdapter adapter = new SearchAdapter(personsToDisplay, eventsToDisplay);
        searchRecyclerView.setAdapter(adapter);
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {

        private final ArrayList<Person> persons;
        private final ArrayList<Event> events;

        SearchAdapter(ArrayList<Person> persons, ArrayList<Event> events) {
            this.persons = persons;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position) {
            return position < persons.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < persons.size()) {
                holder.bind(persons.get(position));
            }
            else {
                holder.bind(events.get(position - persons.size()));
            }
        }

        @Override
        public int getItemCount() {
            return persons.size() + events.size();
        }

    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView top;
        private final TextView bottom;
        private final ImageView icon;

        private Person person;
        private Event event;

        private final int viewType;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            top = itemView.findViewById(R.id.listItemTop);
            bottom = itemView.findViewById(R.id.listItemBottom);
            icon = itemView.findViewById(R.id.listItemIcon);
        }

        private void bind(Person person) {
            this.person = person;

            String name = person.getFirstName() + " " + person.getLastName();
            top.setText(name);

            bottom.setText("");

            if (person.getGender().equals("m")) {
                icon.setImageResource(R.drawable.ic_person_solid);
            }
            else {
                icon.setImageResource(R.drawable.ic_person_dress_solid);
            }
        }

        private void bind(Event event) {
            this.event = event;

            String eventDetails = event.getEventType() + ": " + event.getCity() + ", " +
                    event.getCountry() + "(" + event.getYear() + ")";
            top.setText(eventDetails);

            DataCache dataCache = DataCache.getInstance();
            Person associated = dataCache.getPeopleByID().get(event.getPersonID());
            String nameDetails = associated.getFirstName() + " " + associated.getLastName();
            bottom.setText(nameDetails);

            icon.setImageResource(R.drawable.ic_location_dot_solid);
        }

        @Override
        public void onClick(View view) {
            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                intent.putExtra(PersonActivity.PERSON_ID_KEY, person.getPersonID());
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                intent.putExtra(EventActivity.EVENT_ID_KEY, event.getEventID());
                startActivity(intent);
            }
        }

    }

}