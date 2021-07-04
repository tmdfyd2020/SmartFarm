package org.techtown.smartfarm.Full;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.smartfarm.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<String> id;
    List<String> temp;
    List<String> condition;

    public Adapter(List<String> id, List<String> temp, List<String> condition) {
        this.id = id;
        this.temp = temp;
        this.condition = condition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pig, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.pigId.setText(id.get(position));
        holder.pigTemp.setText(temp.get(position));
    }

    @Override
    public int getItemCount() {
        return id.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pigId, pigTemp;
        View view;
        CardView mCardView;
        ImageView imageView, pig_warning, pig_healing;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pigId = itemView.findViewById(R.id.pig_id);
            pigTemp = itemView.findViewById(R.id.pig_temp);
            mCardView = itemView.findViewById(R.id.pigCard);
            imageView = itemView.findViewById(R.id.pig_state_img);
            pig_warning = itemView.findViewById(R.id.pig_warning_img);
            pig_healing = itemView.findViewById(R.id.pig_healing_img);
            view = itemView;
        }
    }

}
