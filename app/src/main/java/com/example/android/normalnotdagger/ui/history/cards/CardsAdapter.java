package com.example.android.normalnotdagger.ui.history.cards;


import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.normalnotdagger.R;
import com.example.android.normalnotdagger.models.new_model.categ_model.Card;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardsViewHolder> {
    List<Card> cards = new ArrayList<>();
    int pag = 20;
    String id;
    CardsPresentr presentr;
    CardMVP mvp;


    CardsAdapter(CardsPresentr presentr, String id, CardMVP mvp) {
        this.presentr = presentr;
        this.id = id;
        this.mvp = mvp;
    }

    void addCards(List<Card> cards) {
        for (Card i : cards) {
            this.cards.add(i);
        }
    }


    @Override
    public CardsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_fragment, parent, false);
        return new CardsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardsViewHolder holder, final int position) {
        if(position == (pag-6)){
            presentr.getCars(id,pag);
            pag+=20;
        }
        holder.title.setText(cards.get(position).getTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.text.setText(Html.fromHtml(cards.get(position).getText(),Html.FROM_HTML_MODE_LEGACY));
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 mvp.itemClick(cards.get(position));
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    public class CardsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.layout)
        LinearLayout layout;


        public CardsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    
}
