package com.example.samuel.musicmaster5003;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.Arrays;

import groovy.lang.Closure;
import groovy.transform.CompileStatic;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

public class SpectrogramMaker {

    private static int getColor(float power) {
        float[] hsv = new float[3];
        int amount = (int) (power * 256);
        Color.RGBToHSV(amount, amount, amount, hsv);
        return Color.HSVToColor(hsv);
    }

    public static Spectrogram makeSpectrogram(Activity activity, String fileName, boolean inCache) throws java.io.IOException {
        ReadWAV2Array audioTest = new ReadWAV2Array(activity, fileName, inCache);
        double[] rawData = audioTest.getByteArray();
        int length = rawData.length;

        final int MAX_FREQ_NEEDED = 1500;
        final int WINDOW_SIZE = (int)Math.pow(2, 11);
        final int OVERLAP_FACTOR = 8;
        final int WINDOW_STEP = WINDOW_SIZE / OVERLAP_FACTOR;

        //calculate FFT parameters
        double sampleRate = audioTest.getSamplingRate();
        double highestDetectableFrequency = sampleRate / 2.0;
        int rateRatio = ((int)highestDetectableFrequency) / MAX_FREQ_NEEDED;
        int lowestFrequency = 351;//(int)(5.0 * (sampleRate / rateRatio) / WINDOW_SIZE);
        int step = length / rateRatio;

        double[] temp = new double[step];
        for (int i = 0; i < length - rateRatio; i += rateRatio) {
            temp[i / rateRatio] = rawData[i];
        }
        rawData = temp;
        length = rawData.length;

        //initialize plotData array
        int nX = (length - WINDOW_SIZE) / WINDOW_STEP;
        int nY = WINDOW_SIZE;
        double[][] plotData = new double[nX][WINDOW_SIZE / 2];

        double maxAmp = Double.MIN_VALUE;
        double minAmp = Double.MAX_VALUE;

        double ampSquare;

        FastFourierTransform fft = new FastFourierTransform(WINDOW_SIZE);

        double[] im = new double[rawData.length];
        Arrays.fill(im, 0.0);

        for (int i = 0; i < nX; i++) {
            double[] transformed = fft.fft(Arrays.copyOfRange(rawData, i * WINDOW_STEP, i * WINDOW_STEP + nY), im);
            for (int j = 0; j < transformed.length / 2; j += 2) {
                ampSquare = Math.max(Math.pow(transformed[j], 2) + Math.pow(transformed[j + 1], 2), 0);
                plotData[i][j / 2] = ampSquare;
                maxAmp = Math.max(ampSquare, maxAmp);
                minAmp = Math.min(ampSquare, minAmp);
            }
        }

        //Normalization
        double diff = maxAmp - minAmp;
        for (int i = 0; i < plotData.length; i++) {
            for (int j = 0; j < plotData[0].length; j++) {
                plotData[i][j] = (plotData[i][j] - minAmp) / diff;
            }

        }
        return new Spectrogram(createBitmap(plotData), plotData, lowestFrequency, MAX_FREQ_NEEDED);
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
}
