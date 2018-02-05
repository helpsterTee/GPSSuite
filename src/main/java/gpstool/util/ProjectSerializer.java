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
package gpstool.util;

import com.gluonhq.maps.MapLayer;
import gpstool.model.GPSDataStorage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Thomas
 */
public class ProjectSerializer {
    
    public static Object packProject(GPSDataStorage storage, List<MapLayer> layers, Map<String,String> settings) {
        Project pr = new Project();
        pr.layers = layers;
        pr.settings = settings;
        pr.storage = storage;
        return pr;
    }

    public static void saveGZIPObject(Object o, String fname) {
        FileOutputStream fos = null;
        GZIPOutputStream gos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(fname);
            gos = new GZIPOutputStream(fos);
            oos = new ObjectOutputStream(gos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            gos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static Object loadGZipObject(String fname) {
        Object obj = null;
        FileInputStream fis = null;
        GZIPInputStream gis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(fname);
            gis = new GZIPInputStream(fis);
            ois = new ObjectInputStream(gis);
            obj = ois.readObject();
            ois.close();
            gis.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }
}
