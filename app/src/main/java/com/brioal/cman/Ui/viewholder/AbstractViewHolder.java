package com.brioal.cman.Ui.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.brioal.cman.entity.DeviceEntity;

/**
 * Created by brioal on 16-11-4.
 * Email : brioal@foxmail.com
 * Github : https://github.com/Brioal
 */

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    public AbstractViewHolder(View itemView) {
        super(itemView);
    }

     public abstract void bindView(DeviceEntity entity,int position);
}
