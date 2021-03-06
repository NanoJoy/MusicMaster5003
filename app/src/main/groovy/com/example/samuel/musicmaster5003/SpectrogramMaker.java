package com.example.samuel.musicmaster5003;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Arrays;

public class SpectrogramMaker {

    private static int getColor(float power) {
        float[] hsv = new float[3];
        int amount = (int) (power * 256);
        Color.RGBToHSV(amount, amount, amount, hsv);
        return Color.HSVToColor(hsv);
    }

    public static Spectrogram makeSpectrogram(double[] data, int sampleRate) {
        double[] rawData = data;
        int length = rawData.length;

        final int MAX_FREQ_NEEDED = 1500;
        final int WINDOW_SIZE = (int)Math.pow(2, 11);
        final int OVERLAP_FACTOR = 8;
        final int WINDOW_STEP = WINDOW_SIZE / OVERLAP_FACTOR;

        double highestDetectableFrequency = sampleRate / 2.0;
        int rateRatio = ((int)highestDetectableFrequency) / MAX_FREQ_NEEDED;
        int lowestFrequency = 351;//(int)(5.0 * (sampleRate / rateRatio) / WINDOW_SIZE);
        int compressedSize = length / rateRatio;

        double[] temp = new double[compressedSize];
        for (int i = 0; i < length - rateRatio; i += rateRatio) {
            temp[i / rateRatio] = rawData[i];
        }
        rawData = temp;
        length = rawData.length;

        //initialize plotData array
        int nX = (length - WINDOW_SIZE) / WINDOW_STEP;
        int nY = WINDOW_SIZE;
        double[][] plotData = new double[nX][WINDOW_SIZE / 2];

        double ampSquare;

        FastFourierTransform fft = new FastFourierTransform(WINDOW_SIZE);

        double[] im = new double[rawData.length];
        Arrays.fill(im, 0.0);

        for (int i = 0; i < nX; i++) {
            double[] windowed = triangularWindow(Arrays.copyOfRange(rawData, i * WINDOW_STEP, i * WINDOW_STEP + nY));
            double[] transformed = fft.fft(windowed, im);
            for (int j = 0; j < transformed.length / 2; j += 2) {
                ampSquare = Math.pow(transformed[j], 2) + Math.pow(transformed[j + 1], 2);
                plotData[i][j / 2] = ampSquare;
            }
        }
        return new Spectrogram(null, plotData, lowestFrequency, MAX_FREQ_NEEDED);
    }

    private static Bitmap createBitmap(double[][] plotData) {
        Bitmap theImage = Bitmap.createBitmap(plotData.length, plotData[0].length, Bitmap.Config.ARGB_8888);
        double ratio;
        for (int x = 0; x < plotData.length; x++) {
            for (int y = 0; y < plotData[0].length; y++) {
                ratio = plotData[x][y];
                int newColor = getColor((float)(ratio));
                theImage.setPixel(x, plotData[0].length - y - 1, newColor);
            }
        }

        return theImage;
    }

    private static double[] triangularWindow(double[] arr) {
        double[] windowed = new double[arr.length];
        final int N = arr.length;
        for (int i = 0; i < arr.length; i++) {
            windowed[i] = arr[i] * (1 - Math.abs((i - ((N - 1) / 2)) / (N / 2)));
        }
        return windowed;
    }
}
