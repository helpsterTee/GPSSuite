/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstool.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Thomas
 */
public interface GPSEntry extends Comparable<GPSEntry>, Serializable {
    public Date getDate();
}
