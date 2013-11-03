package de.arnepoths.pixelme;

public class PixelMeUtil {

	static private int[] getKernel(int src[], int posX, int posY, int size,
			int width, int height) {

		int xWert, yWert, pos;
		int kernelDimension = (int) Math.sqrt(size);
		int[] kernel = new int[size];
		int i = 0;
		for (int offsetY = 0; offsetY < kernelDimension; offsetY++) {
			for (int offsetX = 0; offsetX < kernelDimension; offsetX++) {

				yWert = offsetY;
				xWert = offsetX;

				if (posY + offsetY < 0 || posY + offsetY > height - 1) {
					yWert = 0;
				}
				if (posX + offsetX < 0 || posX + offsetX > width - 1) {
					xWert = 0;
				}

				pos = (posY + yWert) * width + (posX + xWert);

				kernel[i] = src[pos];
				i++;
			}
		}

		return kernel;

	}

	static int[] filter(int[] pixels, int width, int height, int dimension) {
		int x, y;
		int out[] = new int[pixels.length];

		int kernelSize = dimension * dimension;
		int kernelDimension = (int) Math.sqrt(kernelSize);

		for (y = 0; y < height; y += kernelDimension) {
			for (x = 0; x < width; x += kernelDimension) {
				int pos = 0;

				int[] kernel = getKernel(pixels, x, y, kernelSize, width,
						height);

				int rValue = 0;
				int gValue = 0;
				int bValue = 0;
				for (int i = 0; i < kernel.length; i++) {
					rValue += (kernel[i] >> 16) & 0xFF;
					gValue += (kernel[i] >> 8) & 0xFF;
					bValue += (kernel[i]) & 0xFF;
				}
				int rMean = rValue / kernel.length;
				int gMean = gValue / kernel.length;
				int bMean = bValue / kernel.length;

				for (int j = 0; j < kernel.length / 2; j++) {
					for (int k = 0; k < kernel.length / 2; k++) {
						if (j + y > height && k + x < width) {
							pos = y * width + (k + x);
						} else if (j + y > height && k + x > width) {
							pos = y * width + x;
						} else if (j + y < height && k + x > width) {
							pos = (j + y) * width + x;
						} else if (j + y < height && k + x < width) {
							pos = (j + y) * width + (k + x);
						}

						out[pos] = (0xFF << 24) | (rMean << 16) | (gMean << 8)
								| bMean;
					}
				}// end for jk
			}
		} // end xy
		return out;
	}

}
