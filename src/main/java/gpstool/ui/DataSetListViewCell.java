/*
 * The MIT License
 *
 * Copyright 2016 Computing in Engineering, Ruhr-University Bochum.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package gpstool.ui;

import gpstool.model.GPSDataSet;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

/**
 *
 * @author Thomas
 */
public class DataSetListViewCell extends ListCell<GPSDataSet> {

    GPSDataSet lastItem;
    HBox hbox = new HBox();
    CheckBox cbox = new CheckBox();
    Label label = new Label();
    boolean checked = true;

    public DataSetListViewCell() {
        super();
        hbox.getChildren().addAll(cbox, label);
        
        cbox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            setChecked(newValue);
        });
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    public boolean getChecked(boolean checked) {
        return this.checked;
    }

    @Override
    protected void updateItem(GPSDataSet item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);  // No text in label of super class
        if (empty) {
            lastItem = null;
            setGraphic(null);
        } else {
            lastItem = item;
            label.setText(item != null ? item.getId() : "<null>");
            if (item.getCol() != null) {
                label.setTextFill(item.getCol());
            }
            setGraphic(hbox);
        }
    }
}
