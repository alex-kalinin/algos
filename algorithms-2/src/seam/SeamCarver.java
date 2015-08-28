package seam;
import edu.princeton.cs.algs4.Picture;
import java.awt.*;
//========================================================================================
// SeamCarver
//========================================================================================
public class SeamCarver
{
	private int _width;
	private int _height;
	private Picture     _picture;
	private Color[][]   _matrix;
	private boolean     _changed;
	private boolean     _transposed;
	//------------------------------------------------------------------------------------
	public SeamCarver(Picture picture)                // create a seam carver object based on the given picture
	{
		_picture = picture;
		_matrix = create_picture_matrix(_picture);
		_width = _picture.width();
		_height = _picture.height();
		_changed = false;
		_transposed = false;
	}
	//------------------------------------------------------------------------------------
	private static Color[][] create_picture_matrix(Picture picture)
	{
		Color[][] result = new Color[picture.width()][picture.height()];
		for (int x = 0; x < picture.width(); x++)
			for (int y = 0; y < picture.height(); y++)
				result[x][y] = picture.get(x, y);
		return result;
	}
	//------------------------------------------------------------------------------------
	public Picture picture()                          // current picture
	{
		if (_changed) {
			_picture = picture_from_matrix(_matrix, _transposed);
			_changed = false;
		}
		return _picture;
	}
	//------------------------------------------------------------------------------------
	private Picture picture_from_matrix(Color[][] matrix)
	{
	}
	//------------------------------------------------------------------------------------
	public int width()                            // width of current picture
	{

	}
	//------------------------------------------------------------------------------------
	public int height()                           // height of current picture
	{

	}
	//------------------------------------------------------------------------------------
	public  double energy(int x, int y)               // energy of pixel at column x and row y
	{
		return is_border(x, y) ? 1000 : calculate_energy(x ,y);
	}
	//------------------------------------------------------------------------------------
	int grad(Color p1, Color p2)
	{
		return  delta_squared(p1.getRed(), p2.getRed())
		      + delta_squared(p1.getGreen(), p2.getGreen())
		      + delta_squared(p1.getBlue(), p2.getBlue());
	}
	//------------------------------------------------------------------------------------
	private static int delta_squared(int color_a, int color_b)
	{
		return (color_b - color_a) * (color_b - color_a);
	}
	//------------------------------------------------------------------------------------
	private double calculate_energy(int x, int y)
	{
		double x_grad = grad(_matrix[x-1][y], _matrix[x+1][y]);
		double y_grad = grad(_matrix[x][y-1], _matrix[x][y+1]);
		return Math.sqrt(x_grad + y_grad);
	}
	//------------------------------------------------------------------------------------
	private boolean is_border(int x, int y)
	{
		return     x <= 0
				|| y <= 0
				|| x >= _picture.width() - 1
				|| y >= _picture.height() - 1;
	}
	//------------------------------------------------------------------------------------
	public int[] findHorizontalSeam()               // sequence of indices for horizontal seam
	{

	}
	//------------------------------------------------------------------------------------
	public int[] findVerticalSeam()                 // sequence of indices for vertical seam
	{

	}
	//------------------------------------------------------------------------------------
	public void removeHorizontalSeam(int[] seam)   // remove horizontal seam from current picture
	{
		remove_seam(seam, false);
	}
	//------------------------------------------------------------------------------------
	private void remove_seam(int[] seam, boolean is_vertical)
	{
		if (is_vertical && _transposed) {
			_matrix = transpose(_matrix);
			_transposed = false;
		}
		else if (!is_vertical && !_transposed)
		{
			_matrix = transpose(_matrix);
			_transposed = true;
		}

		for (int y = 0; y < seam.length; y++)
		{
			int x = seam[y];
			Color[] line = _matrix[x];
			System.arraycopy(line, y + 1, line, y, _width - y - 1);

		}

		if (is_vertical)
			_width -= 1;
		else
			_height -= 1;

		_changed = true;
	}
	//------------------------------------------------------------------------------------
	public void removeVerticalSeam(int[] seam)     // remove vertical seam from current picture
	{
		remove_seam(seam, true);
	}
}
