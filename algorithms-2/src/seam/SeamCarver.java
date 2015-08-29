package seam;
import edu.princeton.cs.algs4.Picture;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
	private Picture picture_from_matrix(Color[][] matrix, boolean transposed)
	{
		Picture result = new Picture(width(), height());
		for (int x = 0; x < _width; x++)
			for (int y = 0; y < _height; y++) {
				int col = transposed ? y : x;
				int row = transposed ? x : y;
				result.set(col, row, matrix[x][y]);
			}
		return result;
	}
	//------------------------------------------------------------------------------------
	public int width()                            // width of current picture
	{
		return _transposed ? _height : _width;
	}
	//------------------------------------------------------------------------------------
	public int height()                           // height of current picture
	{
		return _transposed ? _width : _height;
	}
	//------------------------------------------------------------------------------------
	public double energy(int x, int y)               // energy of pixel at column x and row y
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
		throw new NotImplementedException();
	}
	//------------------------------------------------------------------------------------
	public int[] findVerticalSeam()                 // sequence of indices for vertical seam
	{
		throw new NotImplementedException();
	}
	//------------------------------------------------------------------------------------
	public void removeHorizontalSeam(int[] seam)   // remove horizontal seam from current picture
	{
		remove_seam(seam, false);
	}
	//------------------------------------------------------------------------------------
	// Converts to the horizontal seam if necessary and removes it.
	// Removing a horizontal seam is much faster than a vertical seam.
	//------------------------------------------------------------------------------------
	private void remove_seam(int[] seam, boolean is_vertical)
	{
		if (is_vertical && !_transposed) {
			_matrix = transpose(_matrix);
		}
		else if (!is_vertical && _transposed)
		{
			_matrix = transpose(_matrix);
		}

		for (int x = 0; x < seam.length; x++)
		{
			int y = seam[x];
			Color[] line = _matrix[x];
			System.arraycopy(line, y + 1, line, y, _height - y - 1);
		}

		_height -= 1;
		_changed = true;
	}
	//------------------------------------------------------------------------------------
	private Color[][] transpose(Color[][] matrix)
	{
		Color[][] result = new Color[_height][_width];

		for (int x = 0; x < _width; x++)
			for (int y = 0; y < _height; y++)
				result[y][x] = matrix[x][y];
		int temp = _height;

		//noinspection SuspiciousNameCombination
		_height = _width;
		_width = temp;

		_transposed = !_transposed;

		return result;
	}
	//------------------------------------------------------------------------------------
	public void removeVerticalSeam(int[] seam)     // remove vertical seam from current picture
	{
		remove_seam(seam, true);
	}

	public static void main(String[] args)
	{
		String[] test_args = new String[]{expand_vars("~/Downloads/test_image.jpg"), "100", "0"};
//		ResizeDemo.main();
//		PrintEnergy.main(test_args);
//		ShowEnergy.main(test_args);
		Picture picture = new Picture(test_args[0]);
		SeamCarver seam_carver = new SeamCarver(picture);

		int[] seam = new int[seam_carver.height()];
		for (int i = 0; i < seam.length; i++)
			seam[i] = 10;

		// Remove 50 seams
		for (int i = 0; i < 50; i++)
		{
			seam_carver.removeVerticalSeam(seam);
		}

		Picture result_picture = seam_carver.picture();
		result_picture.show();
	}
	//------------------------------------------------------------------------------------
	private static String expand_vars(String path)
	{
		return path.replaceFirst("^~",System.getProperty("user.home"));
	}
}
