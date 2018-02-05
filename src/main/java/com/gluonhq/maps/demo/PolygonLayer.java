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
package com.gluonhq.maps.demo;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPolygon;
import gpstool.model.Coordinate;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polyline;
import javafx.util.Pair;

/**
 *
 * A layer that allows to visualise points of interest.
 */
public class PolygonLayer extends MapLayer implements Serializable {
    
    private static final double _POLYGON_STROKE_WIDTH = 2;
    private static final double _POLYGON_STROKE_DASH_ARRAY_1 = 4.;
    private static final double _POLYGON_STROKE_DASH_ARRAY_2 = 4.;

    private ObservableList<Pair<MapPolygon, Polyline>> polys = FXCollections.observableArrayList();
    private int boundaryStart = -1, boundaryEnd = -1;
    private String id;
    private static final long serialVersionUID = 1L;

    public PolygonLayer() {
    }

    public void addPoly(MapPolygon p) {
        Polyline pol = new Polyline();
        pol.setStrokeWidth(_POLYGON_STROKE_WIDTH);
        pol.getStrokeDashArray().add(_POLYGON_STROKE_DASH_ARRAY_1);
        pol.getStrokeDashArray().add(_POLYGON_STROKE_DASH_ARRAY_2);
        pol.setStroke(p.getColor());
        polys.add(new Pair(p, pol));
        this.getChildren().add(pol);
        this.markDirty();
    }

    public int getPointCount(int idx) {
        return polys.get(idx).getKey().getCoordinates().size();
    }

    public void setBoundaries(int start, int end) {
        boundaryStart = start;
        boundaryEnd = end;
        layoutLayer();
    }

    public String getUid() {
        return id;
    }

    public void setUid(String id) {
        this.id = id;
    }

    public int[] getBoundaries() {
        return new int[]{boundaryStart, boundaryEnd};
    }

    public void refresh() {
        this.layoutLayer();
    }

    @Override
    protected void layoutLayer() {
        for (Pair<MapPolygon, Polyline> candidate : polys) {
            MapPolygon m_poly = candidate.getKey();
            Polyline polline = candidate.getValue();

            polline.getPoints().clear();
            if (boundaryStart < 0 && boundaryEnd < 0) {
                for (Coordinate c : m_poly.getCoordinates()) {
                    Point2D mapPoint = baseMap.getMapPoint(c.latitude, c.longtitude);
                    polline.getPoints().addAll(new Double[]{mapPoint.getX(), mapPoint.getY()});
                }
            } else if (boundaryStart > 0 && boundaryEnd > boundaryStart && boundaryStart < m_poly.getCoordinates().size() && boundaryEnd < m_poly.getCoordinates().size()) {
                for (int i = boundaryStart; i <= boundaryEnd; i++) {
                    Coordinate c = m_poly.getCoordinates().get(i);
                    Point2D mapPoint = baseMap.getMapPoint(c.latitude, c.longtitude);
                    polline.getPoints().addAll(new Double[]{mapPoint.getX(), mapPoint.getY()});
                }
            }

            polline.setVisible(true);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        ArrayList<Pair<MapPolygon, List<Double>>> savePolys = new ArrayList<>();
        for (Pair<MapPolygon, Polyline> pair : polys) {
            List<Double> pts = new ArrayList<>();
            pair.getValue().getPoints().stream().forEach((d) -> {
                pts.add(d);
            });
            savePolys.add(new Pair<>(pair.getKey(), pts));
        }
        out.writeObject(savePolys);
        out.writeObject(id);
        out.writeObject(boundaryStart);
        out.writeObject(boundaryEnd);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ArrayList<Pair<MapPolygon, List<Double>>> savePolys = (ArrayList<Pair<MapPolygon, List<Double>>>) in.readObject();

        if (savePolys != null) {
            polys = FXCollections.observableArrayList();
        }

        for (Pair<MapPolygon, List<Double>> pair : savePolys) {
            Polyline pl = new Polyline();
            for (Double d : pair.getValue()) {
                pl.getPoints().add(d);
            }
            pl.setStrokeWidth(_POLYGON_STROKE_WIDTH);
            pl.getStrokeDashArray().add(_POLYGON_STROKE_DASH_ARRAY_1);
            pl.getStrokeDashArray().add(_POLYGON_STROKE_DASH_ARRAY_2);
            pl.setStroke(pair.getKey().getColor());
            pl.setVisible(true);
            this.getChildren().add(pl);
            polys.add(new Pair<>(pair.getKey(), pl));
        }
        id = (String) in.readObject();
        boundaryStart = (Integer) in.readObject();
        boundaryEnd = (Integer) in.readObject();
    }

}
