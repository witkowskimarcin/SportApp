package com.example.sportapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportapp.R;
import com.example.sportapp.interfaces.ClickListener;
import com.example.sportapp.model.Offer;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.util.List;

public class OffersInfoViewAdapter extends RecyclerView.Adapter<OffersInfoViewAdapter.ViewHolder> {
  private List<Offer> offers;
  private Context mContext;
  ClickListener clickListener;

  public OffersInfoViewAdapter(List<Offer> offers, ClickListener listener, Context mContext) {
    this.offers = offers;
    this.clickListener = listener;
    this.mContext = mContext;
  }

  @NonNull
  @Override
  public OffersInfoViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

    View view = layoutInflater.inflate(R.layout.inforow_offer, viewGroup, false);
    return new ViewHolder(view, clickListener);
  }

  @Override
  public void onBindViewHolder(@NonNull OffersInfoViewAdapter.ViewHolder viewHolder, int i) {
    final Offer offer = offers.get(i);
    viewHolder.setName(offer.getTitle());
    viewHolder.setDescription(offer.getDescription());
    if (StringUtils.isNotBlank(offer.getImgBase64())) {
      viewHolder.setPhoto(offer.getImgBase64());
    }
  }

  @Override
  public int getItemCount() {
    return offers == null ? 0 : offers.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener, View.OnLongClickListener {
    private TextView title;
    private TextView description;
    private ImageView photo;
    private Button addButton;
    private WeakReference<ClickListener> listenerRef;
    private CardView row;

    public ViewHolder(@NonNull View itemView, ClickListener listener) {
      super(itemView);
      listenerRef = new WeakReference<>(listener);
      components();
    }

    private void components() {
      title = itemView.findViewById(R.id.name);
      description = itemView.findViewById(R.id.description);
      photo = itemView.findViewById(R.id.photo);
      //            addButton = itemView.findViewById(R.id.addButton);
      //            addButton.setOnClickListener(this);
      itemView.setOnClickListener(this);

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

    public TextView getName() {
      return title;
    }

    public void setName(String name) {
      this.title.setText(name);
    }

    public TextView getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description.setText(description);
    }

    public ImageView getPhoto() {
      return photo;
    }

    public void setPhoto(String photo) {
      if (StringUtils.isNotBlank(photo)) {
        byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        this.photo.setImageBitmap(decodedByte);
      }
    }
  }
}
