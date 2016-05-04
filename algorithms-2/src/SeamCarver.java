import edu.princeton.cs.algs4.Picture;
import java.awt.*;
import java.util.Random;
//==============================================================================
// SeamCarver
//==============================================================================
public class SeamCarver {
    private Picture _picture;
    private Color[][] _pixels;
    private double[][] _energy;
    private int _width;
    private int _height;
    private boolean _transposed;
    //--------------------------------------------------------------------------
    public SeamCarver(Picture picture)                // create a seam carver object based on the given picture
    {
        _picture = picture;

        _width = picture.width();
        _height = picture.height();
        _transposed = false;

        _pixels = new Color[picture.height()][picture.width()];
        _energy = new double[picture.height()][picture.width()];

        for (int y = 0; y < picture.height(); y++)
            for (int x = 0; x < picture.width(); x++)
                _pixels[y][x] = picture.get(x, y);

        for (int y = 0; y < picture.height(); y++)
            for (int x = 0; x < picture.width(); x++)
                _energy[y][x] = energy(x, y);
    }
    //--------------------------------------------------------------------------
    public Picture picture()                          // current picture
    {
        if (_transposed) {
            transpose();
        }

        return _picture != null ? _picture : (_picture = create_picture());
    }
    //--------------------------------------------------------------------------
    private Picture create_picture() {
        Picture result = new Picture(_width, _height);
        for (int y = 0; y < _height; y++)
            for (int x = 0; x < _width; x++)
                result.set(x, y, _pixels[y][x]);
        return result;
    }
    //--------------------------------------------------------------------------
    public     int width()                            // width of current picture
    {
        return _width;
    }
    //--------------------------------------------------------------------------
    public     int height()                           // height of current picture
    {
        return _height;
    }
    //--------------------------------------------------------------------------
    public  double energy(int x, int y)               // energy of pixel at column x and row y
    {
        if (x < 0 || x > _width - 1) throw new IndexOutOfBoundsException("x: " + x + ", width: " + _width);
        if (y < 0 || y > _height - 1) throw new IndexOutOfBoundsException("y: " + y + ", height: " + _height);

        double result = 1000;

        // If an interior pixel, calculate the energy
        if (   x > 0 && x < _width - 1
            && y > 0 && y < _height - 1) {

            double gradX2 = grad_squared(_pixels[y][x - 1], _pixels[y][x + 1]);
            double gradY2 = grad_squared(_pixels[y - 1][x], _pixels[y + 1][x]);
            result = Math.sqrt(gradX2 + gradY2);
        }

        return result;
    }
    //--------------------------------------------------------------------------
    private double grad_squared(Color left, Color right) {
        int dR = right.getRed()   - left.getRed();
        int dG = right.getGreen() - left.getGreen();
        int dB = right.getBlue()  - left.getBlue();

        return dR * dR + dG * dG + dB * dB;
    }
    //--------------------------------------------------------------------------
    public int[] findHorizontalSeam()               // sequence of indices for horizontal seam
    {
        if (!_transposed) transpose();
        return do_find_vertical_seam();
    }
    //--------------------------------------------------------------------------
    public void removeHorizontalSeam(int[] seam)   // remove horizontal seam from current picture
    {
        if (!_transposed) transpose();
        do_remove_vertical_seam(seam);
    }
    //--------------------------------------------------------------------------
    private void transpose() {
        Color[][] pixel_result = new Color[_width][_height];
        double[][] energy_result = new double[_width][_height];

        for (int y = 0; y < _height; y++)
            for (int x = 0; x < _width; x++) {
                pixel_result[x][y] = _pixels[y][x];
                energy_result[x][y] = _energy[y][x];
            }

        int temp = _width;
        //noinspection SuspiciousNameCombination
        _width = _height;
        _height = temp;

        _pixels = pixel_result;
        _energy = energy_result;

        _picture = null;
        _transposed = !_transposed;
    }
    //--------------------------------------------------------------------------
    public int[] findVerticalSeam()                 // sequence of indices for vertical seam
    {
        if (_transposed) transpose();
        return do_find_vertical_seam();
    }
    //--------------------------------------------------------------------------
    public void removeVerticalSeam(int[] seam)     // remove vertical seam from current picture
    {
        if (_transposed) transpose();
        do_remove_vertical_seam(seam);
    }
    //--------------------------------------------------------------------------
    private int[] do_find_vertical_seam() {
        int x = new Random().nextInt(_width);
        int[] result = new int[_height];
        for (int y = 0; y < _height; y++) result[y] = x;
        return result;
    }
    //--------------------------------------------------------------------------
    private void do_remove_vertical_seam(int[] seam) {
        if (seam == null) throw new NullPointerException();
        if (seam.length != _height) throw new IllegalArgumentException();

        int prev_x = -1;

        for (int y = 0; y < _height; y++) {
            int seam_x = seam[y];

            if (seam_x < 0 || seam_x >= _width)
                throw new IllegalArgumentException("x differ more than 1 for y = " + y);

            // "x" diff is more than one; not a valid seam
            if (y > 0 && Math.abs(prev_x - seam_x) > 1)
                throw new IllegalArgumentException("x differ more than 1 for y = " + y);

            System.arraycopy(_pixels[y], seam_x + 1, _pixels[y], seam_x, _width - seam_x - 1);

            prev_x = seam_x;
        }

        // Invalidate the picture cache
        _picture = null;
        _width -= 1;
    }

//    private static class Coord
//    {
//        public Coord(int x, int y) {
//        }
//    }
    //==========================================================================
    //
    //==========================================================================
//    private static class MatrixSP {
//
//        private DirectedEdge[] _edgeTo;
//        private double[] _distTo;
//
//        public MatrixSP(double[][] energy, int width, int height) {
//            // Topological order is implied by coordinates:
//            // First "x", then "y"
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    for (Coord v : adj(x, y, width, height)) {
//                        relax(v, energy);
//                    }
//                }
//            }
//        }
//
//        private void relax(Coord v) {
//
//        }
//
//        private Coord[] adj(int x, int y, int width, int height) {
//            ArrayList<Coord> result = new ArrayList<>();
//            if (y < height - 1) {
//                for (int dx = -1; dx <= 1; dx ++) {
//                    int vx = x + dx;
//                    if (vx >= 0 && vx < width) {
//                        Coord v = new Coord(vx, y + 1);
//                        result.add(v);
//                    }
//                }
//            }
//            return (Coord[]) result.toArray();
//        }
//    }
}
