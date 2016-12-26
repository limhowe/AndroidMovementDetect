package com.quandary.quandary.ui.draweritem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.holder.ColorHolder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.model.AbstractDrawerItem;
import com.quandary.quandary.R;

import java.util.List;

import java.util.List;

public class HeaderDrawerItem extends AbstractDrawerItem<HeaderDrawerItem, HeaderDrawerItem.ViewHolder> {
    protected ImageHolder icon;
    protected ImageHolder selectedIcon;

    protected boolean iconTinted = false;

    protected ColorHolder iconColor;
    protected ColorHolder selectedIconColor;
    protected ColorHolder disabledIconColor;

    /**
     * will tint the icon with the default (or set) colors
     * (default and selected state)
     *
     * @param iconTintingEnabled
     * @return
     */
    public HeaderDrawerItem withIconTintingEnabled(boolean iconTintingEnabled) {
        this.iconTinted = iconTintingEnabled;
        return this;
    }

    @Deprecated
    public HeaderDrawerItem withIconTinted(boolean iconTinted) {
        this.iconTinted = iconTinted;
        return this;
    }

    /**
     * for backwards compatibility - withIconTinted..
     *
     * @param iconTinted
     * @return
     */
    @Deprecated
    public HeaderDrawerItem withTintSelectedIcon(boolean iconTinted) {
        return withIconTintingEnabled(iconTinted);
    }


    public boolean isIconTinted() {
        return iconTinted;
    }

    public ImageHolder getIcon() {
        return icon;
    }

    public ImageHolder getSelectedIcon() {
        return selectedIcon;
    }


    public ColorHolder getDisabledIconColor() {
        return disabledIconColor;
    }

    public ColorHolder getSelectedIconColor() {
        return selectedIconColor;
    }

    public ColorHolder getIconColor() {
        return iconColor;
    }

    @Override
    public int getType() {
        return R.id.material_drawer_item_icon_only;
    }

    @Override
    @LayoutRes
    public int getLayoutRes() {
        return R.layout.drawer_header;
    }

    @Override
    public void bindView(ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);
        onPostBindView(this, viewHolder.itemView);
    }

    @Override
    public ViewHolderFactory getFactory() {
        return new ItemFactory();
    }

    public static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        protected ImageView icon;

        private ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}
