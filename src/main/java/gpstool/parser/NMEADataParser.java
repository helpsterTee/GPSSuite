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
package gpstool.parser;

import gpstool.model.GPRMCEntry;
import gpstool.model.GPSDataSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas
 */
public class NMEADataParser implements DataParser {
    
    public static AtomicInteger counter = new AtomicInteger(0);

    @Override
    public GPSDataSet parseGPSFile(File f) {
        GPSDataSet ret = new GPSDataSet();
        ret.setId(""+counter.get()+"_"+f.getName());

        try {
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                processLine(sc.nextLine(), ret);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NMEADataParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    private void processLine(String line, GPSDataSet set) {
        String[] split = line.split(",");
        switch (split[0]) {
            case "$GPRMC":
                processGPRMCData(split, set);
                break;
        }
    }

    private void processGPRMCData(String[] split, GPSDataSet set) {
        String time = split[1];
        String status = split[2];
        String lat = split[3];
        String latori = split[4];
        String lon = split[5];
        String lonori = split[6]; //1 is fix, 0 is no fix 
        Float speed = Float.parseFloat(split[7]); //knots 
        Float course = Float.parseFloat(split[8]); // to true north 
        String date = split[9];
        String signalValid = split[12]; // signal integrity Axx valid, Nxx invalid or no signal
        
        if (lat.isEmpty() || lon.isEmpty() || lat.equals("0") && lon.equals("0"))
            return;
        
        DateFormat df = new SimpleDateFormat("ddMMyy HHmmss.SSS", Locale.GERMAN);
        Date dt = null;
        try {
            dt = df.parse(date+" "+time);
        } catch (ParseException ex) {
            Logger.getLogger(NMEADataParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //convert format to decimal degrees
        int dlat = Integer.parseInt(""+lat.charAt(0)+lat.charAt(1));
        int dlon = Integer.parseInt(""+lon.charAt(0)+lon.charAt(1)+lon.charAt(2));
        float mlat = Float.parseFloat(""+lat.subSequence(2, lat.length())) / 60.0f;
        float mlon = Float.parseFloat(""+lon.subSequence(3, lon.length())) / 60.0f;
        
        float rlat = dlat+mlat;
        float rlon = dlon+mlon;
        
        GPRMCEntry e = new GPRMCEntry(rlat, rlon);
        e.setDate(dt);
        set.addGPSEntry(e);
    }

}
