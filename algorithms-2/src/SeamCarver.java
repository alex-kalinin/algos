import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import java.awt.*;
import java.util.ArrayList;
//==============================================================================
// SeamCarver
//==============================================================================
public class SeamCarver {
    private int[][] _pixels;
    private int _width;
    private int _height;
    private boolean _transposed;
    //--------------------------------------------------------------------------
    public SeamCarver(Picture picture)                // create a seam carver object based on the given picture
    {
        _transposed = false;

        _pixels = new int[picture.height()][picture.width()];

        _width = picture.width();
        _height = picture.height();

        for (int y = 0; y < picture.height(); y++)
            for (int x = 0; x < picture.width(); x++)
                _pixels[y][x] = picture.get(x, y).getRGB();
    }
    //--------------------------------------------------------------------------
    public Picture picture()                          // current picture
    {
        if (_transposed) {
            transpose();
        }

        return create_picture();
    }
    //--------------------------------------------------------------------------
    private Picture create_picture() {
        Picture result = new Picture(_width, _height);
        for (int y = 0; y < _height; y++)
            for (int x = 0; x < _width; x++)
                result.set(x, y, new Color(_pixels[y][x]));
        return result;
    }
    //--------------------------------------------------------------------------
    public     int width()                            // width of current picture
    {
        return _transposed ? _height : _width;
    }
    //--------------------------------------------------------------------------
    public     int height()                           // height of current picture
    {
        return _transposed ? _width : _height;
    }
    //--------------------------------------------------------------------------
    public  double energy(int x, int y)               // energy of pixel at column x and row y
    {
        //noinspection SuspiciousNameCombination
        return _transposed
                ? energy_internal(y, x)
                : energy_internal(x, y);
    }
    //--------------------------------------------------------------------------
    private double energy_internal(int x, int y) {
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
    private double grad_squared(int left_int, int right_int) {
        Color left = new Color(left_int);
        Color right = new Color(right_int);
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
    private void transpose() {
        int[][] pixel_result = new int[_width][_height];

        for (int y = 0; y < _height; y++)
            for (int x = 0; x < _width; x++) {
                pixel_result[x][y] = _pixels[y][x];
            }

        int temp = _width;
        //noinspection SuspiciousNameCombination
        _width = _height;
        _height = temp;

        _pixels = pixel_result;
        _transposed = !_transposed;
    }
    //--------------------------------------------------------------------------
    private int[] do_find_vertical_seam() {
        MatrixSP sp = new MatrixSP(_width, _height);
        return sp.seam();
    }
    //--------------------------------------------------------------------------
    private void do_remove_vertical_seam(int[] seam) {
        if (seam == null) throw new NullPointerException();
        if (seam.length != _height) throw new IllegalArgumentException();

        int prev_x = -1;

        for (int y = 0; y < _height; y++) {
            int seam_x = seam[y];

            if (seam_x < 0 || seam_x >= _width)
                throw new IllegalArgumentException("x out of bounds");

            // "x" diff is more than one; not a valid seam
            if (y > 0 && Math.abs(prev_x - seam_x) > 1)
                throw new IllegalArgumentException("x differ more than 1 for y = " + y);

            if (seam_x < _width - 1) {
                System.arraycopy(_pixels[y], seam_x + 1, _pixels[y], seam_x, _width - seam_x - 1);
            }

            prev_x = seam_x;
        }

        _width -= 1;
    }
    //==========================================================================
    // MatrixSP
    //==========================================================================
    private class MatrixSP {
        private final int _width;
        private final int _height;
        private float[] _distTo;
        private int[] _edgeTo;
        private int V;
        //----------------------------------------------------------------------
        public MatrixSP(int width, int height) {
            V = width * height + 2;
            _width = width;
            _height = height;

            _edgeTo = new int[V];
            _distTo = new float[V];

            for (int v  = 0; v < V; v++) {
                _edgeTo[v] = -1;
                _distTo[v] = Float.POSITIVE_INFINITY;
            }

            _distTo[0] = 0;

            // Topological order is implied by coordinates:
            // First "x", then "y"
            for (int v = 0; v < V; v++) {
                for (int w : adj_w(v)) {
                    assert w > 0;
                    relax(v, w);
                }
            }
        }
        //----------------------------------------------------------------------
        private void relax(int v, int w) {
            float weight = 3000;
            if (w < V - 1) {
                int x = vertex_to_x(w);
                int y = vertex_to_y(w);
                weight = (float) energy_internal(x, y);
            }

            assert v < w;

            if (_distTo[w] > _distTo[v] + weight) {
                // System.out.println("Update edge");
                _distTo[w] = _distTo[v] + weight;
                _edgeTo[w] = v;
            }
        }
        //----------------------------------------------------------------------
        // Returns indexes w of adjacent edges
        private int[] adj_w(int v) {

            int[] result = new int[0];

            if (v == 0) {
                // All top layer is adjacent
                result = new int[_width];
                int y = 0;
                for (int x = 0; x < _width; x++)
                    result[x] = xy_to_vertex(x, y);
            }
            else if (v < V - 1) {
                int x = vertex_to_x(v);
                int y = vertex_to_y(v);

                ArrayList<Integer> x_list = new ArrayList<>();

                if (y < _height - 1) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int wx = x + dx;
                        if (wx >= 0 && wx < _width) {
                            x_list.add(wx);
                        }
                    }
                    result = new int[x_list.size()];
                    for (int i = 0; i < result.length; i++)
                        result[i] = xy_to_vertex(x_list.get(i), y + 1);
                }
                else {
                    // The last row, points to the sink
                    result = new int[1];
                    result[0] = V - 1;
                }
            }
            // else it's a sink, the last edge, nothing is out there
            // so we'll return an empty array.
            for (int aResult : result) {
                assert v < aResult;
            }

            return result;
        }
        //----------------------------------------------------------------------
        private int vertex_to_y(int v) {
            if (v <= 0 || v >= V - 1) throw new IllegalArgumentException();
            return (v - 1) / _width;
        }
        //----------------------------------------------------------------------
        private int vertex_to_x(int v) {
            if (v <= 0 || v >= V - 1) throw new IllegalArgumentException("v: " + v);
            int y = vertex_to_y(v);
            return v - (y * _width + 1);
        }
        //----------------------------------------------------------------------
        private int xy_to_vertex(int x, int y) {
            int result = y * _width + x + 1;
            assert result > 0;
            assert result < V - 1;
            return result;
        }
        //----------------------------------------------------------------------
        public int[] seam() {
            int[] result = new int[_height];
            // Skip the first and last edge
            Stack<Integer> path = pathTo(V - 1);

            assert path.size() == _height + 1;

            for (Integer e : path) {
                int v = e;

                if (v > 0 && v < V - 1) {
                    // Interior nodes on the picture
                    int y = vertex_to_y(v);
                    int x = vertex_to_x(v);
                    result[y] = x;
                }
            }

            return result;
        }
        //----------------------------------------------------------------------
        private Stack<Integer> pathTo(int v) {
            Stack<Integer> path = new Stack<>();
            for (int e = _edgeTo[v]; e != -1; e = _edgeTo[e]) {
                path.push(e);
            }
            return path;
        }
    }
}
