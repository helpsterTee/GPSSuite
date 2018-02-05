/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstool.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.scene.paint.Color;

/**
 *
 * @author Thomas
 */
public class GPSDataSet implements Serializable {
    private List<GPSEntry> entries = new ArrayList<>();
    private SortedSet<GPSEntry> sortedEntries = new TreeSet<GPSEntry>();
    private String id; // might be the filename
    private Color col;
    private static final long serialVersionUID = 1L;
    
    private double minlon = Double.MAX_VALUE;
    private double maxlon = Double.MIN_VALUE;
    private double minlat = Double.MAX_VALUE;
    private double maxlat = Double.MIN_VALUE;
    
    private Date minDate = new Date(Long.MAX_VALUE);
    private Date maxDate = new Date(Long.MIN_VALUE);
    
    public GPSDataSet() {
        
    }
    
    public void addGPSEntry(GPSEntry entry) {
        if (entry instanceof GPSPositionEntry) {
            GPSPositionEntry e = (GPSPositionEntry)entry;
            if (e.getLat() < minlat)
                minlat = e.getLat();
            if (e.getLat() > maxlat)
                maxlat = e.getLat();
            if (e.getLon() < minlon)
                minlon = e.getLon();
            if (e.getLon() > maxlon)
                maxlon = e.getLon();
            if (entry.getDate().before(minDate))
                minDate = entry.getDate();
            if (entry.getDate().after(maxDate))
                maxDate = entry.getDate();
        }
        
        entries.add(entry);
        sortedEntries.add(entry);
    }
    
    /**
     *
     * @return Array of Coordinates (minlat, minlon)-(maxlat, maxlon)
     */
    public Coordinate[] getBounds() {
        return new Coordinate[]{new Coordinate(minlat, minlon), new Coordinate(maxlat, maxlon)};
    }
    
    public List<GPSEntry> getEntries() {
        return entries;
    }
    
    public SortedSet<GPSEntry> getSortedEntries() {
        return sortedEntries;
    }
    
    public Date getMinDate() {
        return minDate;
    }
    
    public Date getMaxDate() {
        return maxDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public Color getCol() {
        return col;
    }

    public void setCol(Color col) {
        this.col = col;
    }
}
