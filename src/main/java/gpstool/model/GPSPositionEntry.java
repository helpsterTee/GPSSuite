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
package gpstool.model;

import java.util.Date;

/**
 *
 * @author Thomas
 */
public class GPSPositionEntry implements GPSEntry {
    
    private Date date;
    private double lat, lon;
    private double cepRadius;
    private static final long serialVersionUID = 1L;

    public GPSPositionEntry(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
    
    public GPSPositionEntry(double lat, double lon, double cepRadius) {
        this.lat = lat;
        this.lon = lon;
        this.cepRadius = cepRadius;
    }
    
    @Override
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date d) {
        this.date = d;
    }
    
    public double getCEP() {
        return cepRadius;
    }
    
    public void setCEP(double cepRadius) {
        this.cepRadius = cepRadius;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * @param lat the lat to set
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * @return the lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * @param lon the lon to set
     */
    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public int compareTo(GPSEntry o) {
        if (this.getDate().before(o.getDate())) {
            return 1;
        } else if (this.getDate().after(o.getDate())) {
            return -1;
        } else {
            return 0;
        }
    }
}
