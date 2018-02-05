/*
 * Copyright (c) 2016, Gluon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.maps;

import gpstool.model.Coordinate;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.paint.Color;

/**
 *
 */
public class MapPolygon implements Serializable {

    private ArrayList<Coordinate> coordinates = new ArrayList<>();
    private double to_latitude, to_longitude;
    private Color col;
    private static final long serialVersionUID = 1L;
    
    public MapPolygon(Coordinate[] cords, Color col) {
        coordinates.addAll(Arrays.asList(cords));
        this.col = col;
    }
    
    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }
    
    public Color getColor() {
        return col;
    }
    
    public void update(ArrayList<Coordinate> cords) {
        this.coordinates = cords;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(coordinates);
        out.writeObject(to_latitude);
        out.writeObject(to_longitude);
        out.writeObject(col.getHue());
        out.writeObject(col.getSaturation());
        out.writeObject(col.getBrightness());
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        coordinates = (ArrayList<Coordinate>)in.readObject();
        to_latitude = (Double)in.readObject();
        to_longitude = (Double)in.readObject();
        double h = (Double)in.readObject();
        double s = (Double)in.readObject();
        double b = (Double)in.readObject();
        col = Color.hsb(h, s, b);
    }
    
}
