/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstool.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Thomas
 */
public class GPSDataStorage implements Serializable{
    private ObservableList<GPSDataSet> sets = FXCollections.observableArrayList();
    private static final long serialVersionUID = 1L;
    
    public GPSDataStorage() {
        
    }
    
    public void addSet(GPSDataSet val) {
        sets.add(val);
    }
    
    public List<GPSDataSet> getSets() {
        return sets;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(new ArrayList<GPSDataSet>(sets));
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ArrayList<GPSDataSet> list = (ArrayList<GPSDataSet>)in.readObject();
        if (list != null) {
            sets = FXCollections.observableArrayList();
        }
        sets.addAll(list);
    }
    
}
