package com.example.sportapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportapp.R;
import com.example.sportapp.interfaces.ClickListener;
import com.example.sportapp.model.Place;

import java.lang.ref.WeakReference;
import java.util.List;

public class InfoViewAdapter extends RecyclerView.Adapter<InfoViewAdapter.ViewHolder> {
    private List<Place> places;
    private Context mContext;
    ClickListener clickListener;

    public InfoViewAdapter(List<Place> places, ClickListener listener, Context mContext) {
        this.places = places;
        this.clickListener = listener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public InfoViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        View view = layoutInflater.inflate(R.layout.inforow, viewGroup, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewAdapter.ViewHolder viewHolder, int i) {
        final Place place = places.get(i);
        viewHolder.setName(place.getName());
    }

    @Override
    public int getItemCount() {
        return places == null ? 0 : places.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView name;
        //        private TextView description;
//        private ImageView photo;
//        private TextView date;
//        private Button addButton;
        private WeakReference<ClickListener> listenerRef;
        private CardView row;

        public ViewHolder(@NonNull View itemView, ClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            components();
        }

        private void components() {
            name = itemView.findViewById(R.id.name);
//            description = itemView.findViewById(R.id.description);
//            photo = itemView.findViewById(R.id.photo);
//            date = itemView.findViewById(R.id.date);

//            addButton = itemView.findViewById(R.id.addButton);
//            addButton.setOnClickListener(this);
//            itemView.setOnClickListener(this);

            row = itemView.findViewById(R.id.row);
            row.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }


//        public TextView getDate() {
//            return date;
//        }
//
//        public void setDate(Date date) {
//            Format formatter = new SimpleDateFormat("dd/MM/yyyy");
//            String s = formatter.format(date);
//            this.date.setText(s);
//        }

        public TextView getName() {
            return name;
        }

        public void setName(String name) {
            this.name.setText(name);
        }
//
//        public TextView getDescription() {
//            return description;
//        }
//
//        public void setDescription(String description) {
//            this.description.setText(description);
//        }
//
//        public ImageView getPhoto() {
//            return photo;
//        }
//
//        public void setPhoto(Integer photo) {
//            this.photo.setImageResource(photo);
//        }
    }
}
