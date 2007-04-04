/*
 * ImgResize.java
 *
 * Created on 23. Januar 2003, 12:22
 */

package org.deegree.model.gridprocessing;

/**
 *
 * @author  <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @version 22.1.2003
 */
public class ImgResize {
    
    private float[][] inData = null;
    private float[][] outData = null;
    
    public ImgResize(float[][] data, int newWidth, int newHeight) {
        this.inData = data;
        outData = new float[newWidth][newHeight];
    }
    
    private int sign(int x) {
        int k = ( (x) > 0 ? 1:-1);
        return k;
    }
    
   /*
    * Stretches a horizontal source line onto a horizontal
    * destination line. Used by RectStretch.
    * Entry:
    *    x1,x2 - x-coordinates of the destination line
    *    y1,y2 - x-coordinates of the source line
    *    yr    - y-coordinate of source line
    *    yw    - y-coordinate of destination line
    */
    private void stretch(int x1,int x2,int y1,int y2,int yr,int yw) {
        int dx,dy,e,d,dx2;
        int sx,sy;
        float value = 0;
        dx = Math.abs(x2-x1);
        dy = Math.abs(y2-y1);
        sx = sign(x2-x1);
        sy = sign(y2-y1);
        e=( dy << 1 ) - dx;
        dx2 = dx << 1;
        dy = dy << 1;
        for( d = 0; d <= dx; d++ ) {
            value = inData[yr][y1];
            outData[yw][x1] = value;
            while(e >= 0 ) {
                y1 += sy;
                e -= dx2;
            }
            x1 += sx;
            e += dy;
        }
    }
    
    /**
     * RectStretch enlarges or diminishes a source rectangle of
     * a bitmap to a destination rectangle. The source
     * rectangle is selected by the two points (xs1,ys1) and
     * (xs2,ys2), and the destination rectangle by (xd1,yd1) and
     * (xd2,yd2). Since readability of source-code is wanted,
     * some optimizations have been left out for the reader:
     * It�s possible to read one line at a time, by first
     * stretching in x-direction and then stretching that bitmap
     * in y-direction.
     * Entry:
     * xs1,ys1 - first point of source rectangle
     * xs2,ys2 - second point of source rectangle
     * xd1,yd1 - first point of destination rectangle
     * xd2,yd2 - second point of destination rectangle
     */
    public float[][] rectStretch() {
        
        int xs1 = 0;
        int ys1 = 0;
        int xs2 = inData[0].length-1;
        int ys2 = inData.length-1;
        int xd1 = 0;
        int yd1 = 0;
        int xd2 = outData[0].length-1;
        int yd2 = outData.length-1;
        
        int dx,dy,e,d,dx2;
        int sx,sy;
        dx = Math.abs(yd2-yd1);
        dy = Math.abs(ys2-ys1);
        sx = sign(yd2-yd1);
        sy = sign(ys2-ys1);
        e =(dy << 1) - dx;
        dx2 = dx << 1;
        dy = dy << 1;
        for(d = 0; d <= dx; d++) {
            stretch(xd1,xd2,xs1,xs2,ys1,yd1);
            while( e >= 0) {
                ys1 += sy;
                e -= dx2;
            }
            yd1 += sx;
            e += dy;
        }
        return outData;
    }
    
    public float[][] simpleStretch() {
        double dx = inData[0].length / (double)outData[0].length;
        double dy = inData.length / (double)outData.length;
        
        double py = 0.0;        
        for (int y = 0; y < outData.length; y++) {
            double px = 0.0;
            for (int x = 0; x < outData[0].length; x++) {                
                float v = inData[ (int)py ][ (int)py];
                outData[ y ][ x ] = v;                
                px += dx;
            }
            py += dy;
        }
        return outData;
    }
    
    
    
}
/* ********************************************************************
Changes to this class. What the people have been up to:
$Log: ImgResize.java,v $
Revision 1.2  2006/07/12 14:46:19  poth
comment footer added

********************************************************************** */
