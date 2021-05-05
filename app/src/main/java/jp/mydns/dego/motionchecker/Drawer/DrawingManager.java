package jp.mydns.dego.motionchecker.Drawer;

import android.app.Activity;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import jp.mydns.dego.motionchecker.Util.DebugLog;
import jp.mydns.dego.motionchecker.View.PaintViewController;

public class DrawingManager {
    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "DrawingManager";

    // ---------------------------------------------------------------------------------------------
    // private fields
    // ---------------------------------------------------------------------------------------------
    private final PaintViewController viewController;
    private DrawItemBase.DrawType drawType;
    private DrawItemBase.ColorType colorType;
    private final List<DrawItemBase> drawItems;
    private int lastIndex;

    private final int[] colorTable = {
        Color.argb(0x50, 0xFF, 0xFF, 0xFF), /* white */
        Color.argb(0x50, 0xFF, 0x00, 0x00), /* red */
        Color.argb(0x50, 0x00, 0xFF, 0x00), /* green */
        Color.argb(0x50, 0x00, 0x00, 0xFF), /* blue */
        Color.argb(0x50, 0xFF, 0xFF, 0x00), /* yellow */
        Color.argb(0x50, 0x00, 0x00, 0x00), /* black */
    };

    // ---------------------------------------------------------------------------------------------
    // constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * DrawingManager
     */
    public DrawingManager() {
        DebugLog.d(TAG, "DrawingManager");

        this.viewController = new PaintViewController();
        this.drawType = DrawItemBase.DrawType.Path;
        this.colorType = DrawItemBase.ColorType.Red;
        this.drawItems = new ArrayList<>();
        this.lastIndex = 0;
    }

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * setViews
     *
     * @param activity activity
     */
    public void setViews(Activity activity) {
        DebugLog.d(TAG, "setViews");
        this.viewController.setViews(activity);

        this.viewController.changeDrawTypeImageResource(this.drawType);
        this.viewController.changeColorImageResource(this.colorType);
        this.viewController.setUndoEnabled(false);
        this.viewController.setRedoEnabled(false);
    }

    /**
     * changeDrawable
     *
     * @param drawable is drawable
     */
    public void changeDrawable(boolean drawable) {
        DebugLog.d(TAG, "changeDrawable");
        this.viewController.changeDrawable(drawable);
    }

    /**
     * setDrawType
     *
     * @param type draw type
     */
    public void setDrawType(DrawItemBase.DrawType type) {
        DebugLog.d(TAG, "setDrawType");
        this.drawType = type;

        this.viewController.changeDrawTypeImageResource(type);
    }

    /**
     * getDrawType
     *
     * @return draw type
     */
    public DrawItemBase.DrawType getDrawType() {
        DebugLog.d(TAG, "getDrawType");
        return this.drawType;
    }

    /**
     * setColor
     *
     * @param type color type
     */
    public void setColor(DrawItemBase.ColorType type) {
        DebugLog.d(TAG, "setColor");
        this.colorType = type;

        this.viewController.changeColorImageResource(type);
    }

    /**
     * getColor
     *
     * @return color
     */
    public int getColor() {
        DebugLog.d(TAG, "getColor");
        return colorTable[this.colorType.ordinal()];
    }

    /**
     * undo
     */
    public void undo() {
        DebugLog.d(TAG, "undo");
        if (this.lastIndex > 0) {
            this.lastIndex--;
            this.viewController.redraw();
        } else {
            DebugLog.e(TAG, "undo index error");
        }

        if (this.lastIndex <= 0) {
            this.viewController.setUndoEnabled(false);
        }
        if (this.lastIndex < this.drawItems.size()) {
            this.viewController.setRedoEnabled(true);
        }
    }

    /**
     * redo
     */
    public void redo() {
        DebugLog.d(TAG, "redo");
        if (this.lastIndex < this.drawItems.size()) {
            this.lastIndex++;
            this.viewController.redraw();
        } else {
            DebugLog.e(TAG, "redo index error");
        }

        if (this.lastIndex >= this.drawItems.size()) {
            this.viewController.setRedoEnabled(false);
        }
        if (this.lastIndex > 0) {
            this.viewController.setUndoEnabled(true);
        }
    }

    /**
     * clear
     */
    public void clear() {
        DebugLog.d(TAG, "clear");
        this.viewController.clear();
        this.drawItems.clear();
    }

    /**
     * addDrawItem
     *
     * @param item draw item
     */
    public void addDrawItem(DrawItemBase item) {
        DebugLog.d(TAG, "addDrawItem");
        if (this.lastIndex < this.drawItems.size()) {
            for (int index = this.drawItems.size(); index > this.lastIndex; index--) {
                this.drawItems.remove(index - 1);
            }
        }
        this.drawItems.add(item);
        this.lastIndex++;

        this.viewController.setUndoEnabled(true);
        this.viewController.setRedoEnabled(false);
    }

    /**
     * getActiveDrawItems
     *
     * @return active draw items
     */
    public List<DrawItemBase> getActiveDrawItems() {
        return this.drawItems.subList(0, this.lastIndex);
    }

    // ---------------------------------------------------------------------------------------------
    // private method
    // ---------------------------------------------------------------------------------------------

}
